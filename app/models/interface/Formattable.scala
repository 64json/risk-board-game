package models.interface

import common.Utils._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

trait Formattable {
  def fields: Map[String, Any]

  def apply(keyObjs: Any*): JsValue = format(keyObjs: _*)

  def format(keyObjs: Any*): JsValue = {
    val formattedFields: Seq[(String, JsValueWrapper)] = keyObjs.map(keyObj => {
      var jsonKey: String = null
      var jsonValue: JsValueWrapper = null
      keyObj match {
        case (key: String, keyObjs: List[Any]) =>
          jsonKey = key
          val value = fields.get(key)
          if (value.isEmpty) throw new Error("The key is not found")
          jsonValue = value.get match {
            case None => JsNull

            case v: Identifiable => v(keyObjs: _*)
            case Some(v: Identifiable) => v(keyObjs: _*)
            case v: Seq[Identifiable] => v.map(_ (keyObjs: _*))
            case Some(v: Seq[Identifiable]) => v.map(_ (keyObjs: _*))

            case _ => throw new Error("The type cannot be populated.")
          }
        case key: String =>
          jsonKey = key
          val value = fields.get(key)
          if (value.isEmpty) throw new Error("The key is not found")
          jsonValue = value.get match {
            case None => JsNull

            case v: Identifiable => onlyId(v)
            case Some(v: Identifiable) => onlyId(v)
            case v: Seq[Identifiable] => onlyIds(v)
            case Some(v: Seq[Identifiable]) => onlyIds(v)

            case v: String => Json.toJson(v)
            case Some(v: String) => Json.toJson(v)
            case v: Boolean => Json.toJson(v)
            case Some(v: Boolean) => Json.toJson(v)
            case v: Int => Json.toJson(v)
            case Some(v: Int) => Json.toJson(v)

            case _ => throw new Error("The type cannot be serialized.")
          }
      }
      (jsonKey, jsonValue)
    })
    Json.obj(formattedFields: _*)
  }
}

object Formattable {

  implicit object FormattableFormat extends Format[Formattable] {
    override def writes(formattable: Formattable): JsValue = formattable.format()

    override def reads(json: JsValue): JsResult[Formattable] = ???
  }

}