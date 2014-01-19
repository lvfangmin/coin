package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Registration extends Controller {

  val regForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("The user already exist", result => result match {
      case (email, password) => User.regist(email, password)
    })
  )

  def index = Action { implicit request => 
    Ok(html.regist(regForm))
  }

  def regist = Action { implicit request =>
    regForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.regist(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("email" -> user._1)
    )
  }
}
