package models

import models.interface.Identifiable

import scala.util.Random

class Attack(val fromTerritory: Territory, val toTerritory: Territory, val attackingDiceCount: Int) extends Identifiable {
  var defendingDiceCount: Option[Int] = None
  var attackingDice: Option[String] = None
  var defendingDice: Option[String] = None
  var done: Boolean = false

  def defend(defendingDiceCount: Int) = {
    this.defendingDiceCount = Some(defendingDiceCount)
    val attackingDice = Seq.fill(attackingDiceCount)(Random.nextInt(6) + 1)
    val defendingDice = Seq.fill(defendingDiceCount)(Random.nextInt(6) + 1)
    this.attackingDice = Some(attackingDice.mkString(""))
    this.defendingDice = Some(defendingDice.mkString(""))
    val sortedAttackingDice = attackingDice.sortWith(_ > _)
    val sortedDefendingDice = defendingDice.sortWith(_ > _)
    val minDiceCount = Math.min(attackingDiceCount, defendingDiceCount)
    var rolledDiceCount = 0
    while (rolledDiceCount < minDiceCount && toTerritory.armies > 0) {
      val attackingDie = sortedAttackingDice(rolledDiceCount)
      val defendingDie = sortedDefendingDice(rolledDiceCount)
      if (attackingDie > defendingDie) {
        toTerritory.armies -= 1
      } else {
        fromTerritory.armies -= 1
      }
      rolledDiceCount += 1
    }
    if (toTerritory.armies == 0) {
      val survivedArmies = attackingDiceCount - rolledDiceCount
      toTerritory.owner = fromTerritory.owner
      fromTerritory.armies -= survivedArmies
      toTerritory.armies = survivedArmies
    }
    done = true
  }

  override def fields = Map(
    "id" -> id,
    "fromTerritory" -> fromTerritory,
    "toTerritory" -> toTerritory,
    "attackingDiceCount" -> attackingDiceCount,
    "defendingDiceCount" -> defendingDiceCount,
    "attackingDice" -> attackingDice,
    "defendingDice" -> defendingDice,
    "done" -> done,
  )
}