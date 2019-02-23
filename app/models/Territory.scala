package models

import common.Utils._
import models.interface.Identifiable

class Territory(val name: String) extends Identifiable {
  var adjacencyTerritories: List[Territory] = List()
  var owner: Option[Player] = None
  var armies: Option[Int] = None

  def border(territories: Territory*): Unit = {
    adjacencyTerritories ++= territories
  }

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "adjacencyTerritories" -> adjacencyTerritories,
    "owner" -> owner,
    "armies" -> armies,
  )
}
