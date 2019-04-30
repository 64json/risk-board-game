package models

import controllers.Client
import models.interface.{Identifiable, Receivable}

class Player(val name: String, val client: Client) extends Identifiable with Receivable {
  var color: Option[Int] = None
  var assignedArmies: Int = 0
  var allotting: Boolean = false
  var assigning: Boolean = false
  var attacking: Boolean = false
  var fortifying: Boolean = false

  override def fields: Map[String, Any] = Map(
    "id" -> id,
    "name" -> name,
    "color" -> color,
    "assignedArmies" -> assignedArmies,
    "allotting" -> allotting,
    "assigning" -> assigning,
    "attacking" -> attacking,
    "fortifying" -> fortifying
  )

  override def receivers: List[Client] = List(client)
}
