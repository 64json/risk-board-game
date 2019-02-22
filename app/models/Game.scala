package models

import common.Utils._
import controllers.SocketActor
import models.interface.{Formattable, Identifiable, Receivable}
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Game(val name: String, var owner: Player) extends Identifiable with Formattable with Receivable {
  var playing = false
  val players: ArrayBuffer[Player] = ArrayBuffer()
  var turns: Option[List[Player]] = None
  var turnIndex: Option[Int] = None
  var continents: Option[List[Continent]] = None
  join(owner)

  override def format: JsValue = jsonObject(
    "id" -> id,
    "name" -> name,
    "owner" -> onlyId(owner),
    "playing" -> playing,
    "players" -> players,
    "turns" -> onlyIds(turns),
    "turnIndex" -> turnIndex,
    "continents" -> continents,
  )

  override def receivers: ArrayBuffer[Player] = players

  def join(player: Player): Unit = {
    if (player.game.isDefined) throw new Error("The player is already in a game.")
    if (playing) throw new Error("Unable to add a player during the game.")
    if (players.length >= 6) throw new Error("Too many players.")

    players += player
    player.game = Some(this)
  }

  def leave(player: Player): Unit = {
    if (!player.game.contains(this)) throw new Error("The player is not in the game.")

    players -= player
    player.game = None
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
    players.foreach(player => player.assignedArmies = Some(assignedArmies))
    turns = Some(Random.shuffle(players.toList))
    turnIndex = Some(0)
    continents = Some(Continent.createContinents)
  }

  def destroy(): Unit = {
    SocketActor.games -= this
  }
}