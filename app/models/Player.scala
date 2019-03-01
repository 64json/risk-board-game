package models

import controllers.Client
import models.interface.{Identifiable, Receivable}

class Player(val name: String, val client: Client) extends Identifiable with Receivable {
  var assignedArmies: Int = 0

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "assignedArmies" -> assignedArmies,
  )

  override def receivers: List[Client] = List(client)
}