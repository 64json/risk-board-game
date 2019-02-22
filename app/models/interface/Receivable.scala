package models.interface

import common.Utils._
import models.Player
import play.api.libs.json.Json.JsValueWrapper

import scala.collection.mutable.ArrayBuffer

trait Receivable {
  def receivers: ArrayBuffer[Player]

  def send(fields: (String, JsValueWrapper)*): Unit = {
    val response = jsonObject(fields: _*)
    receivers.foreach(_.receiver ! response)
  }
}