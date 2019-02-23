package common

import models.interface.Identifiable
import play.api.libs.json._

object Utils {
  def typed[T0](a: List[JsValue])(implicit t0: Reads[T0]) =
    a(0).as[T0]

  def typedTuple[T0, T1](a: List[JsValue])(implicit t0: Reads[T0], t1: Reads[T1]) =
    (a(0).as[T0], a(1).as[T1])

  def onlyId[T <: Identifiable](obj: T): String = {
    obj.id
  }

  def onlyId[T <: Identifiable](obj: Option[T]): Option[String] = {
    if (obj.isEmpty) return None
    Some(onlyId(obj.get))
  }

  def onlyIds[T <: Identifiable](list: Seq[T]): Seq[String] = {
    list.map(obj => onlyId(obj))
  }

  def onlyIds[T <: Identifiable](list: Option[Seq[T]]): Option[Seq[String]] = {
    if (list.isEmpty) return None
    Some(list.get.map(obj => onlyId(obj)))
  }
}
