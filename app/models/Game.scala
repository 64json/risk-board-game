package models

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Game(val name: String, var owner: Player) extends Identifiable {
  var playing = false
  val players = ListBuffer(owner)
  var turns: List[Player] = _
  var turnIndex: Int = _
  var continents: List[Continent] = _
  var territories: List[Territory] = _

  def addPlayer(player: Player): Unit = {
    if (playing) throw new Error("Unable to add a player during the game.")
    if (players.length >= 6) throw new Error("Too many players.")

    players += player
  }

  def removePlayer(player: Player): Unit = {
    players -= player
    if (players.isEmpty) {
      destroy()
      return
    }
    if (owner == player) {
      owner = players.head
    }
    if (playing) {
      // TODO: reset territories owned by the removed player
      if (players.length == 1) {
        // TODO: the only player wins the game
      }
    }
  }

  def start() = {
    if (players.length < 3) throw new Error("Not enough players.")
    if (players.length > 6) throw new Error("Too many players.")

    playing = true
    val assignedArmies = 20 + (6 - players.length) * 5
    players.foreach(player => player.assignedArmies = assignedArmies)
    turns = Random.shuffle(players.toList)
    turnIndex = 0
    continents = Continent.createContinents
    territories = continents.flatMap(continent => continent.territories)
  }

  def destroy() = {
    // TODO: remove from game list
  }
}
