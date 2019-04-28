package models

import java.io.FileInputStream

import models.interface.Identifiable
import play.api.libs.json.{JsValue, Json}

class Continent(val name: String, val additionalArmies: Int, val territories: List[Territory]) extends Identifiable {
  def apply(number: Int): Territory = territories(number - 1)

  override def fields: Map[String, Any] = Map(
    "id" -> id,
    "name" -> name,
    "additionalArmies" -> additionalArmies,
    "territories" -> territories
  )
}

object Continent {
  def createContinents: List[Continent] = {
    // Refer to https://en.wikipedia.org/wiki/Risk_(game)#/media/File:Risk_game_map_fixed.png
    val stream = new FileInputStream("assets/map.json")
    val json = try {
      Json.parse(stream)
    } finally {
      stream.close()
    }
    val continents = (json \ "continents").as[List[JsValue]].map(continent =>
      new Continent(
        (continent \ "name").as[String],
        (continent \ "additionalArmies").as[Int],
        (continent \ "territories").as[List[JsValue]].map(territory =>
          new Territory(
            (territory \ "name").as[String],
            (territory \ "x").as[Double],
            (territory \ "y").as[Double]
          )
        )
      )
    )
    (json \ "adjacencies").as[List[JsValue]].foreach(adjacency => {
      val (continentIndex, territoryIndex) = (adjacency \ "from").as[(Int, Int)]
      val fromTerritory = continents(continentIndex).territories(territoryIndex)
      (adjacency \ "to").as[List[JsValue]].foreach(to => {
        val (continentIndex, territoryIndex) = to.as[(Int, Int)]
        val toTerritory = continents(continentIndex).territories(territoryIndex)
        fromTerritory.border(toTerritory)
      })
    })
    continents
  }
}
