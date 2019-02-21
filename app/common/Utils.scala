package common

import play.api.libs.json._
import play.api.mvc.Results.Ok

object Utils {
  def toJson[T](obj: T)(implicit tjs: Writes[T]): JsValue = {
    if (obj == null) return JsNull
    Json.toJson(obj)
  }

  def sendJson[T](obj: T)(implicit tjs: Writes[T]) = Ok(Json.toJson(obj))
}
