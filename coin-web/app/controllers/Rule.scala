package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Rule extends Controller with Secured {

  val ruleForm = Form(
    tuple(
      "cointype" -> text,
      "coinvalue" -> text
    ) verifying ("Invalid cointype", result => result match {
      case (cointype, coinvalue) => Coin.isValid(cointype)
    })
  )

  def index(rule: Int) = IsAuthenticated { username => _ =>
    Ok(html.rule(username, rule))
  }

  def list() = IsAuthenticated { username => implicit request =>
    Ok(html.rulelist())
  }

  def add(rule: Int) = IsAuthenticated { username => implicit request =>
    Ok(html.rules.ruleadd(username, rule))
  }

  def saveToDB(cointype: String, value: String) {
  
  }

  def save() = IsAuthenticated { username => implicit request =>
    ruleForm.bindFromRequest().fold(
      formWithError => BadRequest(html.rules.ruleadd(username, 1)),
      ruleInfo => {
        saveToDB(ruleInfo._1, ruleInfo._2)
        Ok(html.dashboard(username, List(1, 2, 3, 5)))
      }
    )
  }
}
