package models.interface

import scala.util.Random

trait Identifiable {
  val id: String = Random.alphanumeric.take(8).mkString("")
}
