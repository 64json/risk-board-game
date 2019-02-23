package models.interface

import scala.util.Random

trait Identifiable extends Formattable {
  val id: String = Random.alphanumeric.take(8).mkString("")
}
