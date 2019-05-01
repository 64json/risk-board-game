package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import common.Utils._
import controllers.Client.{Action, system}
import models.interface.{Identifiable, Receivable}
import models.{Game, Player, Territory}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Client extends Receivable {
  // creates an SocketActor object for each client
  def props(actorRef: ActorRef): Props = Props(new Client(actorRef))

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
    "error" -> error
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
    val territory: Option[Territory] = game.territories.find(_.id == territoryId)
    if (territory.isEmpty) throw new Error("The territory is not found.")
    territory.get
  }

  // processes message received from client
  override def receive: Receive = {
    case msg: JsValue =>
      val action = msg.as[Action]
      try {
        call(action.method, action.args)
      } catch {
        case e: Throwable =>
          e.printStackTrace
          error = Some(e.getMessage)
          send("error")
      }
  }

  // maps method name to the actual method
  def call(method: String, args: List[JsValue]): Unit = {
    method match {
      case "createGame" =>
        val (gameName, ownerName) = typedTuple[String, String](args)
        createGame(gameName, ownerName)

      case "joinGame" =>
        val (gameId, playerName) = typedTuple[String, String](args)
        joinGame(gameId, playerName)

      case "leaveGame" =>
        leaveGame()

      case "startGame" =>
        startGame()

      case "allotArmy" =>
        val territoryId = typed[String](args)
        allotArmy(territoryId)

      case "assignArmies" =>
        val (territoryId, armies) = typedTuple[String, Int](args)
        assignArmies(territoryId, armies)

      case "createAttack" =>
        val (fromTerritoryId, toTerritoryId, attackingDiceCount) = typedTuple[String, String, Int](args)
        createAttack(fromTerritoryId, toTerritoryId, attackingDiceCount)

      case "defend" =>
        val defendingDiceCount = typed[Int](args)
        defend(defendingDiceCount)

      case "endAttack" =>
        endAttack()

      case "fortify" =>
        val (fromTerritoryId, toTerritoryId, armies) = typedTuple[String, String, Int](args)
        fortify(fromTerritoryId, toTerritoryId, armies)

      case "endFortify" =>
        endFortify()

      case "keepAlive" =>

      case _ => throw new Error(s"Unknown method '${method}'.")
    }
  }

  def createGame(gameName: String, ownerName: String): Unit = {
    if (gameName == "") throw new Error("Game name cannot be empty.")
    if (ownerName == "") throw new Error("Player name cannot be empty.")
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
    if (playerName == "") throw new Error("Player name cannot be empty.")
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
        "players" -> List(
          "assignedArmies",
          "allotting",
          "assigning",
        ),
        "owner",
        "turnIndex",
        "continents" -> List(
          "territories" -> List(
            "owner"
          )
        ),
        "attack",
        "winner"
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
        "players" -> List(
          "color",
          "assignedArmies",
          "allotting",
          "assigning",
          "attacking",
          "fortifying"
        ),
        "turnIndex",
        "continents" -> List(
          "name",
          "additionalArmies",
          "territories" -> List(
            "name",
            "flag",
            "x",
            "y",
            "adjacencyTerritories",
            "owner",
            "armies"
          )
        )
      )
    )
  }

  def allotArmy(territoryId: String): Unit = {
    val game = getGame()
    val territory = getTerritory(territoryId)
    if (!player.get.allotting) throw new Error("This is not your turn.")
    game.allotArmy(player.get, territory)

    game.send(
      "game" -> List(
        "players" -> List(
          "assignedArmies",
          "allotting",
          "assigning"
        ),
        "turnIndex",
        "continents" -> List(
          "territories" -> List(
            "owner",
            "armies"
          )
        )
      )
    )
  }

  def assignArmies(territoryId: String, armies: Int): Unit = {
    val game = getGame()
    val territory = getTerritory(territoryId)
    if (!player.get.assigning) throw new Error("This is not your turn.")
    game.assignArmies(player.get, territory, armies)

    game.send(
      "game" -> List(
        "players" -> List(
          "assignedArmies",
          "assigning",
          "attacking"
        ),
        "turnIndex",
        "continents" -> List(
          "territories" -> List(
            "owner",
            "armies"
          )
        )
      )
    )
  }

  def createAttack(fromTerritoryId: String, toTerritoryId: String, attackingDiceCount: Int): Unit = {
    val game = getGame()
    val fromTerritory = game.findTerritory(fromTerritoryId)
    val toTerritory = game.findTerritory(toTerritoryId)
    if (!player.get.attacking) throw new Error("This is not your turn.")
    if (fromTerritory.isEmpty) throw new Error("Invalid attacking territory.")
    if (toTerritory.isEmpty) throw new Error("Invalid defending territory.")
    if (!fromTerritory.get.owner.contains(player.get)) throw new Error("You don't own the attacking territory.")
    if (toTerritory.get.owner.contains(player.get)) throw new Error("You already own the defending territory.")
    game.createAttack(fromTerritory.get, toTerritory.get, attackingDiceCount)

    game.send(
      "game" -> List(
        "attack" -> List(
          "fromTerritory",
          "toTerritory",
          "attackingDiceCount",
          "defendingDiceCount",
          "attackingDice",
          "defendingDice",
          "done"
        )
      )
    )
  }

  def defend(defendingDiceCount: Int): Unit = {
    val game = getGame()
    if (game.attack.isEmpty || game.attack.get.done) throw new Error("There is no ongoing attack.")
    if (!game.attack.get.toTerritory.owner.contains(player.get)) throw new Error("You don't own the defending territory.")
    game.defend(defendingDiceCount)

    game.send(
      "game" -> List(
        "attack" -> List(
          "defendingDiceCount",
          "attackingDice",
          "defendingDice",
          "done"
        )
      )
    )
    system.scheduler.scheduleOnce(3 seconds) {
      game.attack = None
      game.send(
        "game" -> List(
          "players" -> List(
            "attacking",
            "fortifying"
          ),
          "continents" -> List(
            "territories" -> List(
              "owner",
              "armies"
            )
          ),
          "attack",
          "winner"
        )
      )
    }
  }

  def endAttack(): Unit = {
    val game = getGame()
    game.endAttack(player.get)

    game.send(
      "game" -> List(
        "players" -> List(
          "attacking",
          "fortifying"
        )
      )
    )
  }

  def fortify(fromTerritoryId: String, toTerritoryId: String, armies: Int): Unit = {
    val game = getGame()
    val fromTerritory = game.findTerritory(fromTerritoryId)
    val toTerritory = game.findTerritory(toTerritoryId)
    if (!player.get.fortifying) throw new Error("This is not your turn.")
    if (fromTerritory.isEmpty) throw new Error("Invalid attacking territory.")
    if (toTerritory.isEmpty) throw new Error("Invalid defending territory.")
    if (!fromTerritory.get.owner.contains(player.get)) throw new Error("You don't own the territory.")
    if (!toTerritory.get.owner.contains(player.get)) throw new Error("You don't own the territory.")
    game.fortify(fromTerritory.get, toTerritory.get, armies)

    game.send(
      "game" -> List(
        "players" -> List(
          "assignedArmies",
          "assigning",
          "fortifying"
        ),
        "turnIndex",
        "continents" -> List(
          "territories" -> List(
            "armies"
          )
        )
      )
    )
  }

  def endFortify(): Unit = {
    val game = getGame()
    game.endFortify(player.get)

    game.send(
      "game" -> List(
        "players" -> List(
          "assignedArmies",
          "assigning",
          "fortifying"
        ),
        "turnIndex"
      )
    )
  }
}
