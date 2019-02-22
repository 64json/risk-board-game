package models

import common.Utils._
import models.interface.{Formattable, Identifiable}
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

class Territory(val name: String) extends Identifiable with Formattable {
  val adjacencyTerritories: ListBuffer[Territory] = ListBuffer()
  var owner: Option[Player] = None
  var armies: Option[Int] = None

  def border(territories: Territory*): Unit = {
    adjacencyTerritories ++= territories
  }

  override def format: JsValue = jsonObject(
    "id" -> id,
    "name" -> name,
    "adjacencyTerritories" -> onlyIds(adjacencyTerritories),
    "owner" -> onlyId(owner),
    "armies" -> armies,
  )
}
