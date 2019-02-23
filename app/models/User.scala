package models

import akka.actor.ActorRef
import models.interface.{Identifiable, Receivable}

class User(val name: Option[String], val receiver: ActorRef) extends Identifiable with Receivable {
  var game: Option[Game] = None
  var player: Option[Player] = None

  override def receivers: List[User] = List(this)

  override def fields = Map(
    "id" -> id,
    "name" -> name,
    "game" -> game,
    "player" -> player,
  )
}