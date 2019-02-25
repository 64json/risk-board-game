package models.interface

import scala.util.Random

// add id field to object
trait Identifiable extends Formattable {
  val id: String = Random.alphanumeric.take(8).mkString("")
}
