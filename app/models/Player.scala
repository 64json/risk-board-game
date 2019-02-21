package models

import models.interface.{Formattable, Identifiable}
import play.api.libs.json._

class Player(val name: String) extends Identifiable with Formattable {
  // TODO: authenticate players with access token
  var assignedArmies: Int = 0

  override def format: JsValue = Json.obj(
    "id" -> JsString(id),
    "name" -> JsString(name),
    "assignedArmies" -> JsNumber(assignedArmies),
  )
}