package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import common.Utils._
import controllers.Client.Action
import models.interface.{Identifiable, Receivable}
import models.{Game, Player, Territory}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Client extends Receivable {
  // creates an SocketActor object for each client
  def props(actorRef: ActorRef) = Props(new Client(actorRef))

  case class Action(method: String, args: List[JsValue])

  // deserializes JSON message from client
  implicit val ActionReads: Reads[Action] = (
    (JsPath \ "method").read[String] and
      (JsPath \ "args").read[List[JsValue]]
    ) (Action.apply _)

  var games: List[Game] = List()
  var clients: List[Client] = List()

  override def receivers: List[Client] = clients

  def findGame(gameId: String): Game = {
    val option = games.find(game => game.id == gameId)
    if (option.isEmpty) throw new Error("Game not found.")
    option.get
  }

  val system = ActorSystem()
  // periodically sends out the list of rooms to users in lobby
  system.scheduler.schedule(0 seconds, 1 seconds) {
    sendTo(_.game.isEmpty,
      "games" -> List(
        "name",
        "playing",
        "players" -> List(
          "name"
        ),
        "owner"
      )
    )
  }
}

class Client(val actorRef: ActorRef) extends Actor with Identifiable with Receivable {
  var connected = true
  var game: Option[Game] = None
  var player: Option[Player] = None
  var error: Option[String] = None
  Client.clients :+= this

  override def fields: Map[String, Any] = Map(
    "id" -> id,
    "connected" -> connected,
    "games" -> Client.games,
    "game" -> game,
    "player" -> player,
    "error" -> error,
  )

  override def receivers: List[Client] = List(this)

  send("connected")

  // unregisters the user after socket closes
  override def postStop() {
    Client.clients = Client.clients.filter(_ != this)
    if (game.isDefined) leaveGame()

    connected = false
    send("connected")
  }

  def getGame(): Game = {
    if (game.isEmpty) throw new Error("The client is not in a game.")
    game.get
  }

  def getTerritory(territoryId: String): Territory = {
    val game = getGame()
    game.continents.get.foreach(_.territories.foreach(territory => {
      if (territory.id == territoryId) return territory
    }))
    throw new Error("The territory is not found.")
  }

  // processes message received from client
  override def receive: Receive = {
    case msg: JsValue =>
      val action = msg.as[Action]
      try {
        call(action.method, action.args)
      } catch {
        case e: Throwable =>
          error = Some(e.getMessage)
          send("error")
      }
  }

  // maps method name to the actual method
  def call(method: String, args: List[JsValue]): Unit = {
    method match {
      case "createGame" => {
        val (gameName, ownerName) = typedTuple[String, String](args)
        createGame(gameName, ownerName)
      }
      case "joinGame" => {
        val (gameId, playerName) = typedTuple[String, String](args)
        joinGame(gameId, playerName)
      }
      case "leaveGame" => {
        leaveGame()
      }
      case "startGame" => {
        startGame()
      }
      case "assignArmies" => {
        val (territoryId, armies) = typedTuple[String, Int](args)


        assignArmies(territoryId, armies)
      }
      case "proceedWithTurn" => {
        val playerId = typed[String](args)
        proceedWithTurn(playerId)
      }
    }
  }

  def createGame(gameName: String, ownerName: String): Unit = {
    if (this.game.isDefined) throw new Error("The client is already in a game.")
    val game: Game = new Game(gameName, ownerName, this, game => {
      Client.games = Client.games.filter(_ != game)
    })
    Client.games :+= game
    this.game = Some(game)
    this.player = Some(game.owner)

    send("player")
    game.send(
      "game" -> List(
        "name",
        "playing",
        "players" -> List(
          "name"
        ),
        "owner"
      )
    )
  }

  def joinGame(gameId: String, playerName: String): Unit = {
    if (this.game.isDefined) throw new Error("The client is already in a game.")
    val game = Client.findGame(gameId)
    val player = game.join(playerName, this)
    this.game = Some(game)
    this.player = Some(player)

    send("player")
    game.send(
      "game" -> List(
        "name",
        "playing",
        "players" -> List(
          "name"
        ),
        "owner"
      )
    )
  }

  def leaveGame(): Unit = {
    val game = getGame()
    game.leave(player.get)
    this.game = None
    this.player = None

    send("game", "player")
    game.send(
      "game" -> List(
        "players",
        "owner",
        "continents" -> List(
          "territories" -> List(
            "owner",
            "armies"
          )
        )
      )
    )
  }

  def startGame(): Unit = {
    val game = getGame()
    if (game.owner.client != this) throw new Error("Only owner can start the game.")
    game.start()

    game.send(
      "game" -> List(
        "playing",
        "turnIndex",
        "players" -> List(
          "assignedArmies"
        ),
        "turnIndex",
        "continents" -> List(
          "name",
          "territories" -> List(
            "name",
            "adjacencyTerritories",
            "owner",
            "armies"
          )
        )
      )
    )
  }

  def assignArmies(territoryId: String, armies: Int) = {
    val game = getGame()
    val territory = getTerritory(territoryId)
    game.assignArmies(player.get, territory, armies)

    game.send(
      "game" -> List(
        "players" -> List(
          "assignedArmies"
        ),
        "continents" -> List(
          "territories" -> List(
            "owner",
            "armies"
          )
        )
      )
    )
  }

  def proceedWithTurn(playerId: String) = {
    val game = getGame()
    val player = game.players(game.turnIndex.get)
    if(player.id != playerId) throw new Error("diff plaewraewgw")
    game.turnIndex = Some(game.turnIndex.get + 1)
    game.turnIndex = Some(game.turnIndex.get % game.players.length)

    game.send(
      "game" -> List(
        "turnIndex"
      )
    )
  }
}