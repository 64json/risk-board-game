package models

import akka.actor.ActorRef
import common.Utils._
import models.interface.{Formattable, Identifiable, Receivable}
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

class Player(val name: String, val receiver: ActorRef) extends Identifiable with Formattable with Receivable {
  var game: Option[Game] = None
  var assignedArmies: Option[Int] = None

  override def format: JsValue = jsonObject(
    "id" -> id,
    "name" -> name,
    "game" -> onlyId(game),
    "assignedArmies" -> assignedArmies,
  )

  override def receivers: ArrayBuffer[Player] = ArrayBuffer(this)
}