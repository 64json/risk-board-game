package models

import java.util.UUID.randomUUID

class Identifiable {
  val id: String = randomUUID().toString
}
