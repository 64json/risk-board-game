package models

import models.interface.{Identifiable, Receivable}

class Player(val user: User) extends Identifiable with Receivable {
  var assignedArmies: Int = 0

  override def fields = Map(
    "id" -> id,
    "name" -> user.name,
    "assignedArmies" -> assignedArmies,
  )

  override def receivers: List[User] = List(this.user)
}