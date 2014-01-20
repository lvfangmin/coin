package controllers

import play.api._
import play.api.mvc._

import models._
import views._

object Dashboard extends Controller with Secured {
  def index = IsAuthenticated { username => implicit request =>
    Ok(html.dashboard(username, Subscription.getSubscriptions(username)))
  }
}
