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
      // TODO: reset territories owned by the removed player
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
    turnIndex = Some(0)
    continents = Some(Continent.createContinents)
  }

  def destroy(): Unit = onDestroy(this)
}