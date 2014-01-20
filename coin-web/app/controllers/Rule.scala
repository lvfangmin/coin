package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.cache._

import play.Logger

import models._
import views._
import persist.Redis
import common._
import scala.collection.JavaConversions._

import scala.collection.mutable.HashMap

object Rule extends Controller with Secured with Redis {

  val rule1 = RuleTemplate("1", "BTC UPPER BOUND", "alert when btc price is larger than the dedicated price")
  val rule2 = RuleTemplate("2", "BTC LOWER BOUND", "alert when btc price is less than the dedicated price")
  val rule3 = RuleTemplate("3", "LTC UPPER BOUND", "alert when ltc price is larger than the dedicated price")
  val rule4 = RuleTemplate("4", "LTC LOWER BOUND", "alert when ltc price is less than the dedicated price")
  val predefinedTemplates = List(rule1, rule2, rule3, rule4)
  val rulesMap = Map("1" -> rule1, "2" -> rule2, "3" -> rule3, "4" -> rule4)

  val ruleForm = Form(mapping("price" -> nonEmptyText)(RuleParams.apply)(RuleParams.unapply))

  def templates() = IsAuthenticated { username => implicit request =>
      Ok(html.rule.templates(predefinedTemplates))
  }

  def template(ruleId: String) = IsAuthenticated { username => implicit request =>
      Ok(html.rule.template(rulesMap(ruleId)))
  }

  def index(uid: String, sid: String, rid: String, price: String) = IsAuthenticated { username => _ =>
    Ok(html.rule.ruleview(username, (uid, sid, rid, price)))
  }

  def save(ruleId: String) = IsAuthenticated { username => implicit request =>
    Logger.info("save rule {} to {}", ruleId, username)
    ruleForm.bindFromRequest().fold(
      formWithError => BadRequest(html.rule.template(rulesMap(ruleId))),
      ruleParams => {
        Subscription.subscribe(ruleId, username, ruleParams.price)
        Redirect(routes.Dashboard.index)
      }
    )
  }

  def delete(uid: String, sid: String, rid: String, price: String) = IsAuthenticated { username => implicit request =>
    Subscription.delete((uid, sid, rid, price))
    Redirect(routes.Dashboard.index)
  } 
}

case class RuleParams(price: String)
