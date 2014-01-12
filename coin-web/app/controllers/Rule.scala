package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.cache._
import play.api.Play.current
import com.typesafe.plugin.RedisPlugin

import play.Logger

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

  def templates() = IsAuthenticated { username => implicit request =>
    Ok(html.rule.templates())
  }

  def template(rule: Int) = IsAuthenticated { username => implicit request =>
    Ok(html.rule.template(rule))
  }

  def index(id: Int) = IsAuthenticated { username => _ =>
    Ok(html.rule.ruleview(username, id))
  }

  def saveToDB(cointype: String, value: String) {
    val client = new RedisPlugin(current).jedisPool.getResource()
    client.set(cointype, value)
    //Cache.set(cointype, value)
    Logger.info("Save to db {}, {}", cointype, value)
    Logger.info("values in db {}", client.get(cointype))
  }

  def save(id: Int) = IsAuthenticated { username => implicit request =>
    ruleForm.bindFromRequest().fold(
      formWithError => BadRequest(html.rule.template(id)),
      ruleInfo => {
        saveToDB(ruleInfo._1, ruleInfo._2)
        Ok(html.dashboard(username, List(1, 2, 3, 5)))
      }
    )
  }
}
