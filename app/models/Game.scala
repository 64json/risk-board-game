package models

import controllers.SocketActor
import models.interface.{Identifiable, Receivable}

import scala.util.Random

class Game(val name: String, var user: User) extends Identifiable with Receivable {
  var playing = false
  var players: List[Player] = List()
  var owner: Player = join(user)
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

  override def receivers: List[User] = players.map(_.user)

  def join(user: User): Player = {
    if (players.exists(_.user == user)) throw new Error("The user is already in the game.")
    if (playing) throw new Error("Unable to join while playing.")
    if (players.length >= 6) throw new Error("Too many players.")

    val player = new Player(user)
    player.user.game = Some(this)
    player.user.player = Some(player)
    players :+= player
    player
  }

  def leave(player: Player): Unit = {
    if (!players.contains(player)) throw new Error("The player is not in the game.")

    player.user.game = None
    player.user.player = None
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

  def destroy(): Unit = {
    SocketActor.games = SocketActor.games.filter(_ != this)
  }
}