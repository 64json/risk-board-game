package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject._
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }

  implicit val transformer = MessageFlowTransformer.jsonMessageFlowTransformer

  def connect = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      SocketActor.props(out)
    }
  }
}
