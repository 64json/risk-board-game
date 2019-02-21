package controllers

import akka.stream.scaladsl.Flow
import javax.inject._
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def connect = WebSocket.accept[String, String] {
    request => Flow[String].map(_ + " Back")
  }
}
