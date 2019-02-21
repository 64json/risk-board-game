package models

import scala.collection.mutable.ListBuffer

class Territory(val name: String) extends Identifiable {
  val adjacencyTerritories: ListBuffer[Territory] = ListBuffer()
  var owner: Player = _
  var armies = 0

  def border(territories: Territory*): Unit = {
    adjacencyTerritories ++= territories
  }
}
