package models.interface

import models.User
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper

trait Receivable {
  def receivers: List[User]

  def send(fields: (String, JsValueWrapper)*): Unit = {
    val response = Json.obj(fields: _*)
    receivers.foreach(_.receiver ! response)
  }

  def sendTo(filter: User => Boolean, fields: (String, JsValueWrapper)*): Unit = {
    val response = Json.obj(fields: _*)
    receivers.filter(filter).foreach(_.receiver ! response)
  }
}