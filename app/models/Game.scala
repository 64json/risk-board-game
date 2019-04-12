package models

import java.util

import controllers.Client
import models.interface.{Identifiable, Receivable}
import play.api.libs.json.{JsArray, JsValue, Json}

import scala.util.Random

class Game(val name: String, ownerName: String, ownerClient: Client, onDestroy: Game => Unit) extends Identifiable with Receivable {
  var playing = false
  var players: List[Player] = List()
  var owner: Player = join(ownerName, ownerClient)
  var turnIndex: Option[Int] = None
  var continents: Option[List[Continent]] = None

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "playing" -> playing,
    "players" -> players,
    "owner" -> owner,
    "turnIndex" -> turnIndex,
    "continents" -> continents,
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

    players = players.filter(_ != player)
    if (players.isEmpty) {
      destroy()
      return
    }
    if (owner == player) {
      owner = players.head
    }
    if (playing) {
      continents.get.foreach(_.territories.foreach(territory => {
        if (territory.owner.contains(player)) {
          territory.reset()
        }
      }))
      if (players.length == 1) {
        // TODO: the only player wins the game
      }
    }
  }

  def start(): Unit = {
    if (players.length < 3) throw new Error("Not enough players.")
    if (players.length > 6) throw new Error("Too many players.")

    playing = true
    val assignedArmies = 20 + (6 - players.length) * 5
    players.foreach(player => player.assignedArmies = assignedArmies)
    players = Random.shuffle(players)
    turnIndex = None
    continents = Some(Continent.createContinents)
  }

  def nextTurn(): Unit = {
    turnIndex = Some(turnIndex.get + 1)
  }

  def getContinents: Option[List[Continent]] = continents

  def assignArmies(player: Player, territory: Territory, armies: Int): Unit = {
    if (armies < 1) throw new Error("You need to assign at least one dude.")
    if (armies > player.assignedArmies) throw new Error("You do not have enough army dudes available.")
    if (territory.owner.isDefined && territory.owner.get != player) throw new Error(s"This is not ${player.name}'s territory.")
    territory.owner = Some(player)
    territory.armies = Some(territory.armies.getOrElse(0) + armies)
    player.assignedArmies -= armies
    if (turnIndex.isEmpty && players.forall(_.assignedArmies == 0)) {
      turnIndex = Some(0)
      giveArmies()
    }
  }

  def proceedWithTurn(): Unit = {
    //if (players(turnIndex.get).assignedArmies > 0) throw new Error(s"All the armies should be assigned before proceeding with turn.") TODO: Error should only occur at beginning of players turn.
    turnIndex = Some((turnIndex.get + 1) % players.length)
    giveArmies()
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
  }


  def destroy(): Unit = onDestroy(this)
}