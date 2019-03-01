package models

import models.interface.{Identifiable, Receivable}

class Player(val user: User) extends Identifiable with Receivable {
  var assignedArmies: Int = 0

  override def fields = Map(
    "id" -> id,
    "name" -> user.name,
    "assignedArmies" -> assignedArmies,
  )

  def setAssignedArmies(armies: Int) = {
    this.assignedArmies = armies
  }

  def getName: String = user.name

  override def receivers: List[User] = List(this.user)
}