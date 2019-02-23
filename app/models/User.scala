package models

import akka.actor.ActorRef
import models.interface.{Formattable, Receivable}

class User(val name: String, val receiver: ActorRef) extends Formattable with Receivable {
  var game: Option[Game] = None
  var player: Option[Player] = None

  override def receivers: List[User] = List(this)

  override def fields = Map(
    "name" -> name,
    "game" -> game,
    "player" -> player,
  )
}