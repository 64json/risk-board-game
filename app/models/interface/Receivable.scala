package models.interface

import models.User
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper

// enable an object to receive messages
trait Receivable {
  // define receivers
  def receivers: List[User]

  // send message to all receivers defined in the object
  def send(fields: (String, JsValueWrapper)*): Unit = {
    val response = Json.obj(fields: _*)
    receivers.foreach(_.receiver ! response)
  }

  // send message to the filtered receivers
  def sendTo(filter: User => Boolean, fields: (String, JsValueWrapper)*): Unit = {
    val response = Json.obj(fields: _*)
    receivers.filter(filter).foreach(_.receiver ! response)
  }
}