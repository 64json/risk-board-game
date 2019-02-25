package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import common.Utils._
import controllers.SocketActor.{Action, Guest}
import models.interface.Receivable
import models.{Game, User}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object SocketActor extends Receivable {
  // creates an SocketActor object for each client
  def props(receiver: ActorRef) = Props(new SocketActor(receiver))

  case class Action(method: String, args: List[JsValue])

  // deserializes JSON message from client
  implicit val ActionReads: Reads[Action] = (
    (JsPath \ "method").read[String] and
      (JsPath \ "args").read[List[JsValue]]
    ) (Action.apply _)

  var games: List[Game] = List()
  var users: List[User] = List()

  def findGame(gameId: String): Game = {
    val option = games.find(game => game.id == gameId)
    if (option.isEmpty) throw new Error("Game not found.")
    option.get
  }

  override def receivers: List[User] = users

  val system = ActorSystem()
  // periodically sends out the list of rooms to users in lobby
  system.scheduler.schedule(0 seconds, 1 seconds) {
    users.foreach(user => {
      if (user.game.isEmpty) {
        user.send(
          "games" -> games.map(_ (
            "name",
            "playing",
            "players" -> List(
              "name"
            ),
            "owner"
          ))
        )
      }
    })
  }

  // unregistered user
  case class Guest(override val receiver: ActorRef) extends User(None, receiver)

}

class SocketActor(receiver: ActorRef) extends Actor {
  var user: User = Guest(receiver)

  user.send("connected" -> true)

  // unregisters the user after socket closes
  override def postStop() { // TODO: does it work?
    unregister _

    user.send("connected" -> false)
  }

  // processes message received from client
  override def receive: Receive = {
    case msg: JsValue =>
      val action = msg.as[Action]
      try {
        user match {
          case Guest(_) if action.method != "register" => throw new Error("The player must be registered first.")
          case _ => findMethod(action.method)(action.args)
        }
      } catch {
        case e: Error =>
          user.send("error" -> e.getMessage)
      }
  }

  // maps method name to the actual method
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

    user match {
      case Guest(_) =>
        user = new User(Some(playerName), receiver)
        SocketActor.users :+= user

        user.send(
          "user" -> user(
            "name"
          )
        )
      case _ => throw new Error("Already registered.")
    }
  }

  def unregister(args: List[JsValue]): Unit = {
    user match {
      case Guest(_) => throw new Error("Already registered.")
      case _ =>
        SocketActor.users = SocketActor.users.filter(_ != user)
        val player = user.player
        val game = user.game
        if (player.isDefined && game.isDefined) leaveGame(List(JsString(game.get.id)))
        user = Guest(receiver)

        user.send(
          "user" -> user(
            "name"
          )
        )
    }
  }

  def createGame(args: List[JsValue]): Unit = {
    val gameName = typed[String](args)

    val game = new Game(gameName, user)
    SocketActor.games :+= game

    user.send(
      "user" -> user(
        "game",
        "player"
      )
    )
    game.send(
      "game" -> game(
        "name",
        "playing",
        "players" -> List(
          "name"
        ),
        "owner"
      )
    )
  }

  def joinGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    game.join(user)

    user.send(
      "user" -> user(
        "game",
        "player"
      )
    )
    game.send(
      "game" -> game(
        "name",
        "playing",
        "players" -> List(
          "name"
        ),
        "owner"
      )
    )
  }

  def leaveGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    game.leave(user.player.get)

    user.send(
      "user" -> user(
        "game",
        "player"
      ),
      "game" -> JsNull
    )
    game.send(
      "game" -> game(
        "players"
      )
    )
  }

  def startGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    if (game.owner.user != user) throw new Error("Only owner can start the game.")
    game.start()

    game.send(
      "game" -> game(
        "playing",
        "players" -> List(
          "assignedArmies"
        ),
        "continents" -> List(
          "name",
          "territories" -> List(
            "name",
            "adjacencyTerritories"
          )
        )
      )
    )
  }
}