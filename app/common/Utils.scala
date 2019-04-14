package common

import models.interface.Identifiable
import play.api.libs.json._

object Utils {
  // casts values to specified types
  def typed[T0](a: List[JsValue])(implicit t0: Reads[T0]): T0 =
    a(0).as[T0]

  def typedTuple[T0, T1](a: List[JsValue])(implicit t0: Reads[T0], t1: Reads[T1]): (T0, T1) =
    (a(0).as[T0], a(1).as[T1])

  def typedTuple[T0, T1, T2](a: List[JsValue])(implicit t0: Reads[T0], t1: Reads[T1], t2: Reads[T2]): (T0, T1, T2) =
    (a(0).as[T0], a(1).as[T1], a(2).as[T2])

  def typedTuple[T0, T1, T2, T3](a: List[JsValue])(implicit t0: Reads[T0], t1: Reads[T1], t2: Reads[T2], t3: Reads[T3]): (T0, T1, T2, T3) =
    (a(0).as[T0], a(1).as[T1], a(2).as[T2], a(3).as[T3])

  // returns id of identifiable object
  def onlyId[T <: Identifiable](obj: T): String = {
    obj.id
  }

  // maps list of identifiable objects into list of their ids
  def onlyIds[T <: Identifiable](list: Seq[T]): Seq[String] = {
    list.map(obj => onlyId(obj))
  }
}
