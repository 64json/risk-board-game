package models.interface

import controllers.Client

// enable an object to receive messages
trait Receivable {
  // define receivers
  def receivers: List[Client]

  // send message to all receivers defined in the object
  def send(keyObjs: Any*): Unit = {
    receivers.foreach(client => client.actorRef ! client(keyObjs: _*))
  }

  // send message to the filtered receivers
  def sendTo(filter: Client => Boolean, keyObjs: Any*): Unit = {
    receivers.filter(filter).foreach(client => client.actorRef ! client(keyObjs: _*))
  }
}
