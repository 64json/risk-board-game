package controllers

import akka.actor.{Actor, ActorRef, Props}
import common.Utils._
import controllers.SocketActor.Action
import models.interface.Receivable
import models.{Game, Player}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

object SocketActor extends Receivable {
  def props(receiver: ActorRef) = Props(new SocketActor(receiver))

  case class Action(method: String, args: List[JsValue])

  implicit val ActionReads: Reads[Action] = (
    (JsPath \ "method").read[String] and
      (JsPath \ "args").read[List[JsValue]]
    ) (Action.apply _)

  val games: ArrayBuffer[Game] = ArrayBuffer()
  val players: ArrayBuffer[Player] = ArrayBuffer()

  def findGame(gameId: String): Game = {
    val option = games.find(game => game.id == gameId)
    if (option.isEmpty) throw new Error("Game not found.")
    option.get
  }

  override def receivers: ArrayBuffer[Player] = players

  // TODO: periodically send out list of games{id, name, owner, playing, players} and players{id, name, game}
}

class SocketActor(receiver: ActorRef) extends Actor {
  var player: Player = _

  override def postStop() {
    unregister(_)
  }

  override def receive: Receive = {
    case msg: JsValue =>
      val action = msg.as[Action]
      try {
        if (player == null && action.method != "register") throw new Error("The player must be registered first.")
        findMethod(action.method)(action.args)
      } catch {
        case e: Error =>
          e.printStackTrace()
          receiver ! Json.obj(
            "error" -> e.getMessage
          )
      }
  }

  def findMethod(method: String): List[JsValue] => Unit = {
    method match {
      case "register" => register
      case "unregister" => unregister
      case "createGame" => createGame
      case "joinGame" => joinGame
      case "leaveGame" => leaveGame
      case "startGame" => startGame
    }
  }

  def register(args: List[JsValue]): Unit = {
    val playerName = typed[String](args)

    if (player != null) throw new Error("Already registered.")
    player = new Player(playerName, receiver)
    SocketActor.players += player

    player.send(Json.obj(
      "player" -> player
    ))
  }

  def unregister(args: List[JsValue]): Unit = {
    if (player == null) throw new Error("Already unregistered.")
    SocketActor.players -= player
    if (player.game != null) player.game.leave(player)
    player = null

    receiver ! Json.obj(
      "player" -> player
    )
  }

  def createGame(args: List[JsValue]): Unit = {
    val gameName = typed[String](args)

    val game = new Game(gameName, player)
    SocketActor.games += game

    game.send(Json.obj(
      "game" -> game
    ))
  }

  def joinGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    game.join(player)

    game.send(Json.obj(
      "game" -> game
    ))
  }

  def leaveGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    game.leave(player)

    game.send(Json.obj(
      "game" -> game
    ))
  }

  def startGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    if (game.owner != player) throw new Error("Only owner can start the game.")
    game.start()

    game.send(Json.obj(
      "game" -> game
    ))
  }
}