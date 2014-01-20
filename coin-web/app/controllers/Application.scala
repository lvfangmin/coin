package controllers

import play.api._
import play.api.mvc._

import models._
import views._

object Application extends Controller with Secured {

  def index = IsAuthenticated { username => request =>
    Ok(html.dashboard(username, Subscription.getSubscriptions(username)))
  }

}

trait Secured {

  private def username(request: RequestHeader) = request.session.get("email")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Login.index)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}
