package controllers

import play.api._
import play.api.mvc._

import models._
import views._

object Dashboard extends Controller {
  def index = Action { implicit request =>
    Ok(html.dashboard("lvfm", List(1, 2, 3, 4)))
  }
}
