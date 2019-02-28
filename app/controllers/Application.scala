package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject._
import play.api.Configuration
import play.api.http.HttpErrorHandler
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

@Singleton
class Application @Inject()(assets: Assets, errorHandler: HttpErrorHandler, config: Configuration, cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  def index: Action[AnyContent] = assets.at("index.html")

  /*
   creates a static web server
   what is (static) web server? https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_web_server
    */
  def assetOrDefault(resource: String): Action[AnyContent] = if (resource.startsWith(config.get[String]("apiPrefix"))) {
    Action.async(r => errorHandler.onClientError(r, NOT_FOUND, "Not found"))
  } else {
    if (resource.contains(".")) assets.at(resource) else index
  }

  // declares that incoming/outgoing messages are in JSON format
  implicit val transformer: MessageFlowTransformer[JsValue, JsValue] = MessageFlowTransformer.jsonMessageFlowTransformer

  /*
   creates a web socket server
   what is web socket? https://www.maxcdn.com/one/visual-glossary/websocket/
    */
  def ws = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      Client.props(out)
    }
  }
}
