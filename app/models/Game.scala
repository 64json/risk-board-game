package models

import controllers.Client
import models.interface.{Identifiable, Receivable}

import scala.util.Random

class Game(val name: String, ownerName: String, ownerClient: Client, onDestroy: Game => Unit) extends Identifiable with Receivable {
  var playing = false
  var players: List[Player] = List()
  var owner: Player = join(ownerName, ownerClient)
  var turnIndex: Option[Int] = None
  var continents: Option[List[Continent]] = None
  var attack: Option[Attack] = None

  override def fields: Map[String, Any] = Map(
    "id" -> id,
    "name" -> name,
    "playing" -> playing,
    "players" -> players,
    "owner" -> owner,
    "turnIndex" -> turnIndex,
    "continents" -> continents,
    "attack" -> attack
  )

  override def receivers: List[Client] = players.map(_.client)

  def join(playerName: String, playerClient: Client): Player = {
    if (players.exists(_.client == playerClient)) throw new Error("The client is already in the game.")
    if (playing) throw new Error("Unable to join while playing.")
    if (players.length >= 6) throw new Error("Too many players.")
    if (players.exists(_.name == playerName)) throw new Error("The player name is already in use.")

    val player = new Player(playerName, playerClient)
    players :+= player
    player
  }

  def leave(player: Player): Unit = {
    if (!players.contains(player)) throw new Error("The player is not in the game.")

    val playerIndex = players.indexOf(player)
    players = players.filter(_ != player)
    if (turnIndex.isDefined) {
      if (playerIndex < turnIndex.get) {
        turnIndex = Some(turnIndex.get - 1)
      } else if (playerIndex == turnIndex.get) {
        turnIndex = Some(turnIndex.get % players.length)
        if (territories.forall(_.owner.isDefined)) {
          giveArmies()
        } else {
          players(turnIndex.get).allotting = true
        }
      }
    }
    if (players.isEmpty) {
      destroy()
    } else {
      if (owner == player) {
        owner = players.head
      }
      if (playing) {
        // transfer the ownership of each territory to a random player
        territories.filter(_.owner.contains(player)).foreach(_.owner = Some(players(Random.nextInt(players.length))))
        if (players.length == 1) {
          // TODO: the only player wins the game
        }
      }
    }
  }

  def start(): Unit = {
    if (players.length < 3) throw new Error("Not enough players.")
    if (players.length > 6) throw new Error("Too many players.")

    playing = true
    players = Random.shuffle(players)
    val assignedArmies = 20 + (6 - players.length) * 5
    var colors = Random.shuffle(List.range(0, 6))
    players.foreach(player => {
      player.color = Some(colors.head)
      colors = colors.tail
      player.assignedArmies = assignedArmies
    })
    turnIndex = Some(0)
    players(turnIndex.get).allotting = true
    continents = Some(Continent.createContinents)
    /* testing purpose:
    territories.foreach(allotArmy(players(turnIndex.get), _))
    players.foreach(player => assignArmies(player, territories.find(_.owner.contains(player)).get, player.assignedArmies))
    val player = players(turnIndex.get)
    assignArmies(player, territories.find(_.owner.contains(player)).get, player.assignedArmies)
    */
  }

  def territories: List[Territory] = continents.get.flatMap(_.territories)

  def findTerritory(territoryId: String): Option[Territory] = territories.find(_.id == territoryId)

  def allotArmy(player: Player, territory: Territory): Unit = {
    if (territory.owner.isDefined) throw new Error("The territory is already occupied.")
    territory.owner = Some(player)
    territory.armies = 1
    player.assignedArmies -= 1
    player.allotting = false
    if (territories.forall(_.owner.isDefined)) {
      players.foreach(_.assigning = true)
      turnIndex = None
    } else {
      turnIndex = Some((turnIndex.get + 1) % players.length)
      players(turnIndex.get).allotting = true
    }
  }

  def assignArmies(player: Player, territory: Territory, armies: Int): Unit = {
    if (armies < 1) throw new Error("You need to assign at least one dude.")
    if (armies > player.assignedArmies) throw new Error("You do not have enough army dudes available.")
    if (territory.owner.isDefined && territory.owner.get != player) throw new Error("This is not your territory.")
    territory.owner = Some(player)
    territory.armies += armies
    player.assignedArmies -= armies
    if (player.assignedArmies == 0) {
      player.assigning = false
      if (turnIndex.isDefined) {
        player.attacking = true
      } else if (players.forall(_.assignedArmies == 0)) {
        turnIndex = Some(0)
        giveArmies()
      }
    }
  }

  def giveArmies(): Unit = {
    val player = players(turnIndex.get)
    var totalTerritoryCount = 0
    continents.get.foreach(continent => {
      val continentTerritoryCount = continent.territories.count(_.owner.contains(player))
      if (continentTerritoryCount == continent.territories.length) {
        player.assignedArmies = player.assignedArmies + continent.additionalArmies
      }
      totalTerritoryCount = totalTerritoryCount + continentTerritoryCount
    })
    player.assignedArmies = player.assignedArmies + Math.max(3, totalTerritoryCount / 3)
    player.assigning = true
  }


  def createAttack(fromTerritory: Territory, toTerritory: Territory, attackingDiceCount: Int): Unit = {
    if (attack.isDefined && !attack.get.done) throw new Error("There already is an ongoing attack.")
    if (!fromTerritory.adjacencyTerritories.contains(toTerritory)) throw new Error("You can only attack a neighbor territory.")
    if (fromTerritory.armies <= toTerritory.armies) throw new Error("The attacking territory must have at least one more army than the defending territory.")
    if (attackingDiceCount > 3) throw new Error("The attacking dice cannot be more than 3.")
    if (attackingDiceCount < 1) throw new Error("The attacking dice cannot be less than 1.")
    if (attackingDiceCount >= fromTerritory.armies) throw new Error("You must have at least one more army than dice being rolled.")
    attack = Some(new Attack(fromTerritory, toTerritory, attackingDiceCount))
  }

  def defend(defendingDiceCount: Int): Unit = {
    if (defendingDiceCount > 2) throw new Error("The defending dice cannot be more than 2.")
    if (defendingDiceCount < 1) throw new Error("The defending dice cannot be less than 1.")
    if (defendingDiceCount > attack.get.toTerritory.armies) throw new Error("You must have at least as many armies as dice being rolled.")
    attack.get.defend(defendingDiceCount)
  }

  def endAttack(player: Player): Unit = {
    if (attack.isDefined && !attack.get.done) throw new Error("There already is an ongoing attack.")
    player.attacking = false
    player.fortifying = true
  }

  def fortify(fromTerritory: Territory, toTerritory: Territory, armies: Int): Unit = {
    if (!fromTerritory.adjacencyTerritories.contains(toTerritory)) throw new Error("You can only fortify a neighbor territory.")
    if (armies >= fromTerritory.armies) throw new Error("You must leave at least one army.")
    fromTerritory.armies -= armies
    toTerritory.armies += armies
    endFortify(fromTerritory.owner.get)
  }

  def endFortify(player: Player): Unit = {
    player.fortifying = false
    turnIndex = Some((turnIndex.get + 1) % players.length)
    giveArmies()
  }

  def destroy(): Unit = onDestroy(this)
}
