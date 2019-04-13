package models

import controllers.Client
import models.interface.{Identifiable, Receivable}

class Player(val name: String, val client: Client) extends Identifiable with Receivable {
  var assignedArmies: Int = 0
  var assigning: Boolean = false
  var attacking: Boolean = false
  var fortifying: Boolean = false

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "assignedArmies" -> assignedArmies,
    "assigning" -> assigning,
    "attacking" -> attacking,
    "fortifying" -> fortifying,
  )

  override def receivers: List[Client] = List(client)
}