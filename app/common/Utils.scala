package common

import models.interface.Identifiable
import play.api.libs.json._

object Utils {
  // casts values to specified types
  def typed[T0](a: List[JsValue])(implicit t0: Reads[T0]) =
    a(0).as[T0]

  def typedTuple[T0, T1](a: List[JsValue])(implicit t0: Reads[T0], t1: Reads[T1]) =
    (a(0).as[T0], a(1).as[T1])

  // returns id of identifiable object
  def onlyId[T <: Identifiable](obj: T): String = {
    obj.id
  }

  // maps list of identifiable objects into list of their ids
  def onlyIds[T <: Identifiable](list: Seq[T]): Seq[String] = {
    list.map(obj => onlyId(obj))
  }
}
