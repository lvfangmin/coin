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
  val predefinedTemplates = List(rule1, rule2)
  val rulesMap = Map("1" -> rule1, "2" -> rule2)

  val ruleForm = Form(
    tuple(
      "cointype" -> text,
      "coinvalue" -> text
    ) verifying ("Invalid cointype", result => result match {
      case (cointype, coinvalue) => Coin.isValid(cointype)
    })
  )

  def templates() = IsAuthenticated { username => implicit request =>
      Ok(html.rule.templates(predefinedTemplates))
  }

  def template(ruleId: String) = IsAuthenticated { username => implicit request =>
      Ok(html.rule.template(rulesMap(ruleId)))
  }

  def index(id: String) = IsAuthenticated { username => _ =>
    Ok(html.rule.ruleview(username, id))
  }

  def saveToDB(ruleId: String, price: String, username: String) {
  /*
    redis.withJedisClient { implicit client =>
      Logger.info("values in db {}", client.get(cointype))
    }
    Logger.info("Save to db {}, {}", cointype, value)
  */
  }

  def save(ruleId: String) = IsAuthenticated { username => implicit request =>
    Logger.info("Here is save authen")
    ruleForm.bindFromRequest().fold(
      formWithError => BadRequest(html.rule.template(rulesMap(ruleId))),
      ruleInfo => {
        Logger.info("Here is save")
        Subscription.subscribe(ruleId, username, ruleInfo._2)
        Subscription.getSubscriptions(username) map { sub =>
          Logger.info("found subscription with {}", sub) 
        }
        Ok(html.dashboard(username, Subscription.getSubscriptions(username)))
      }
    )
  }
}
