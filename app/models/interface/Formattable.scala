package models.interface

import play.api.libs.json._

trait Formattable {
  def format: JsValue
}

object Formattable {

  implicit object FormattableFormat extends Format[Formattable] {
    override def writes(formattable: Formattable): JsValue = formattable.format

    override def reads(json: JsValue): JsResult[Formattable] = ???
  }

}