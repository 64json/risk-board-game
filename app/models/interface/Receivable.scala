package models.interface

import models.Player
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

trait Receivable {
  def receivers: ArrayBuffer[Player]

  def send(message: JsValue) = receivers.foreach(_.receiver ! message)
}