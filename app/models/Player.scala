package models

import java.util.UUID.randomUUID

class Player(name: String) {
  val id = randomUUID().toString
}
