package common

import models.interface.Identifiable
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import play.api.mvc.Results.Ok

object Utils {
  def toJson[T](obj: T)(implicit tjs: Writes[T]): JsValue = {
    if (obj == null) return JsNull
    Json.toJson(obj)
  }

  def sendJson[T](obj: T)(implicit tjs: Writes[T]) = Ok(Json.toJson(obj))

  def typed[T0](a: List[JsValue])(implicit t0: Reads[T0]) =
    a(0).as[T0]

  def typedTuple[T0, T1](a: List[JsValue])(implicit t0: Reads[T0], t1: Reads[T1]) =
    (a(0).as[T0], a(1).as[T1])

  def onlyId[T <: Identifiable](obj: T): String = {
    if (obj == null) return null
    obj.id
  }

  def onlyIds[T <: Identifiable](list: Seq[T]): Seq[String] = {
    if (list == null) return null
    list.map(onlyId)
  }

  def refineResponse(fields: (String, JsValueWrapper)*): JsObject = {
    val nullableFields = fields.map(tuple => {
      val (key, value) = tuple
      val nullableValue: JsValueWrapper = if (value == null) JsNull else value
      (key, nullableValue)
    })
    Json.obj(nullableFields: _*)
  }
}
