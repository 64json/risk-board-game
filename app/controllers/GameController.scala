package controllers

import common.Utils._
import javax.inject._
import models.{Game, Player}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.collection.mutable.ArrayBuffer

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val games: ArrayBuffer[Game] = ArrayBuffer()

  def list = Action {
    sendJson(Json.obj(
      "games" -> games
    ))
  }

  def get(gameId: String) = Action {
    val game = games.find(game => game.id == gameId).orNull
    if (game == null) {
      NotFound("Game not found.")
    } else {
      sendJson(Json.obj(
        "game" -> game
      ))
    }
  }

  val createForm = Form(
    tuple(
      "ownerName" -> text,
      "gameName" -> text
    )
  )

  def create = Action { implicit request =>
    createForm.bindFromRequest.fold(
      error => BadRequest("Bad request."),
      form => {
        val (gameName, ownerName) = form
        val owner = new Player(ownerName)
        val game = new Game(gameName, owner)
        games += game
        sendJson(Json.obj(
          "game" -> game
        ))
      }
    )
  }

  val joinForm = Form(
    "playerName" -> text
  )

  def join(gameId: String) = Action { implicit request =>
    joinForm.bindFromRequest.fold(
      error => BadRequest("Bad request."),
      form => {
        val playerName = form
        val player = new Player(playerName)
        val game: Game = games.find(game => game.id == gameId).orNull
        if (game == null) {
          NotFound("Game not found.")
        } else {
          game.addPlayer(player)
          sendJson(Json.obj(
            "game" -> game
          ))
        }
      }
    )
  }

  // TODO: let players leave

  def start(gameId: String) = Action {
    val game = games.find(game => game.id == gameId).orNull
    if (game == null) {
      NotFound("Game not found.")
    } else {
      game.start()
      sendJson(Json.obj(
        "game" -> game
      ))
    }
  }
}
