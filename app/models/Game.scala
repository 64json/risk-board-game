package models

import java.util.UUID.randomUUID

import scala.collection.mutable.ListBuffer

class Game(name: String, var owner: Player) {
  val id = randomUUID().toString
  var playing = false
  val players = ListBuffer(owner)
  var territories: List[Territory] = List()

  def addPlayer(player: Player): Unit = {
    players += player
  }

  def removePlayer(player: Player): Unit = {
    players -= player
    if (players.isEmpty) destroy()
    if (owner == player) {
      owner = players.head
    }
  }

  def start() = {
    playing = true
    territories = List.tabulate(42)(i => new Territory(i))
  }

  def destroy() = {

  }
}
