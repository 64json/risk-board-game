package models.interface

import common.Utils._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

/*
 serializes objects/classes into JSON format
 what is JSON? https://www.w3schools.com/whatis/whatis_json.asp
  */
trait Formattable {
  // defines a map from field name to its value
  def fields: Map[String, Any]

  // syntax sugar to call format method
  def apply(keyObjs: Any*): JsValue = format(keyObjs: _*)

  // serializes the specified fields into JSON object
  def format(keyObjs: Any*): JsValue = {
    val formattedFields: Seq[(String, JsValueWrapper)] = ("id" :: keyObjs.toList).map(keyObj => {
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

  // lets Play framework know that this object is serializable
  implicit object FormattableFormat extends Format[Formattable] {
    override def writes(formattable: Formattable): JsValue = formattable.format()

    override def reads(json: JsValue): JsResult[Formattable] = ???
  }

}