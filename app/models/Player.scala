package models

import akka.actor.ActorRef
import models.interface.{Formattable, Identifiable, Receivable}
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

class Player(val name: String, val receiver: ActorRef) extends Identifiable with Formattable with Receivable {
  var game: Game = _
  var assignedArmies: Int = _

  override def format: JsValue = Json.obj(
    "id" -> JsString(id),
    "name" -> JsString(name),
    "game" -> JsString(game.id),
    "assignedArmies" -> JsNumber(assignedArmies),
  )

  override def receivers: ArrayBuffer[Player] = ArrayBuffer(this)
}