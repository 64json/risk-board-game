package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import common.Utils._
import controllers.SocketActor.Action
import models.interface.Receivable
import models.{Continent, Game, User, Territory}
import play.api.libs.functional.syntax._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object SocketActor extends Receivable {
  def props(receiver: ActorRef) = Props(new SocketActor(receiver))

  case class Action(method: String, args: List[JsValue])

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
  system.scheduler.schedule(0 seconds, 1 seconds) {
    users.foreach(user => {
      if (user.game.isEmpty) {
        user.send("games" -> games.map(_ (
          "id",
          "name",
          "playing",
          "players" -> List(
            "id",
            "name"
          ),
          "owner"
        )))
      }
    })
  }
}

class SocketActor(receiver: ActorRef) extends Actor {
  var user: Option[User] = None

  send("connected" -> true)

  def send(fields: (String, JsValueWrapper)*): Unit = {
    val response = Json.obj(fields: _*)
    receiver ! response
  }

  override def postStop() {
    unregister _

    send("connected" -> false)
  }

  override def receive: Receive = {
    case msg: JsValue =>
      val action = msg.as[Action]
      try {
        if (user.isEmpty && action.method != "register") throw new Error("The player must be registered first.")
        findMethod(action.method)(action.args)
      } catch {
        case e: Error =>
          send("error" -> e.getMessage)
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
      case "assignArmies" => assignArmies
    }
  }

  def register(args: List[JsValue]): Unit = {
    val playerName = typed[String](args)

    if (user.isDefined) throw new Error("Already registered.")
    user = Some(new User(playerName, receiver))
    SocketActor.users :+= user.get

    send(
      "user" -> user.get(
        "name"
      )
    )
  }

  def unregister(args: List[JsValue]): Unit = {
    if (user.isEmpty) throw new Error("Already unregistered.")
    SocketActor.users = SocketActor.users.filter(_ != user.get)
    val player = user.get.player
    val game = user.get.game
    if (player.isDefined && game.isDefined) game.get.leave(player.get)
    user = None

    send(
      "user" -> JsNull
    )
  }

  def createGame(args: List[JsValue]): Unit = {
    val gameName = typed[String](args)

    val game = new Game(gameName, user.get)
    SocketActor.games :+= game

    send(
      "user" -> user.get(
        "game",
        "player"
      )
    )
    game.send(
      "game" -> game(
        "id",
        "name",
        "playing",
        "players" -> List(
          "id",
          "name"
        ),
        "owner"
      )
    )
  }

  def joinGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    game.join(user.get)

    send(
      "user" -> user.get(
        "game",
        "player"
      )
    )
    game.send(
      "game" -> game(
        "id",
        "name",
        "playing",
        "players" -> List(
          "id",
          "name"
        ),
        "owner"
      )
    )
  }

  def leaveGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    game.leave(user.get.player.get)

    send(
      "user" -> user.get(
        "game",
        "player"
      ),
      "game" -> JsNull
    )
    game.send(
      "game" -> game(
        "players" -> List(
          "id",
          "name"
        )
      )
    )
  }

  def startGame(args: List[JsValue]): Unit = {
    val gameId = typed[String](args)

    val game = SocketActor.findGame(gameId)
    if (game.owner.user != user.get) throw new Error("Only owner can start the game.")
    game.start()

    game.send(
      "game" -> game(
        "playing",
        "players" -> List(
          "id",
          "name",
          "assignedArmies"
        ),
        "continents" -> List(
          "id",
          "name",
          "territories" -> List(
            "id",
            "name",
            "adjacencyTerritories"
          )
        )
      )
    )
  }

  def getTerritory(game: Game, territory: String): Territory = {
    val myContinents =  game.getContinents.toArray
    println("Territory to find: " + territory.substring(1, territory.length - 1))
    print("my Continents: ")
    println()
    val thisTerritory = territory.substring(1, territory.length - 1)
    for (x <- myContinents) {
      for (y <- x) {
        println(y.name)
        for (z <- y.getTerritories) {
          print("my Territories")
          println(z.name)

          if (z.name.equalsIgnoreCase(thisTerritory)) {
            println("yo")
            return z
          }
        }
        println()
      }
    }
    new Territory("blah")
  }

  def assignArmies(args: List[JsValue]): JsString = {
    val myVals = args.takeRight(2)
    val gameId = typed[String] (args.take(args.length - 2))
    val game = SocketActor.findGame(gameId)
    val army: Int =  typed[Int](myVals.tail)
    val territoryChosen = getTerritory(game, myVals.head.toString())
    try {
      println("We want this one:" + territoryChosen.name)
      println("And this one" + army)
      game.assignArmy(user.get.player.get, territoryChosen, army)
      JsString("Success")
    } catch {
      case e: IllegalArgumentException => println(e.getMessage)
        JsString(e.getMessage)
      case _: Throwable => println("Something unexpected happened!")
        JsString("Something unexpected happened!")
    }
  }
}