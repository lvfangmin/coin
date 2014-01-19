package models

import play.api.cache._
import play.api.Play.current
import com.typesafe.plugin.RedisPlugin
import org.sedis.Dress
import scala.collection.JavaConversions._

import play.Logger

import persist.Redis
import common.Constants

case class User(email: String, password: String)

object User extends Redis {

  def authenticate(email: String, password: String):Boolean = {
    redis.withJedisClient { implicit client =>
      Dress.up(client).get(Constants.USERNAME_UID.replaceAll("%s", email)) match {
        case Some(uid) => {
          if (client.get(Constants.UID_PASSWORD.replaceAll("%s", uid)) == password)
            true
          else
            false
        }
        case None => false
      } 
    }
  }

  def regist(email: String, password: String):Boolean = {
    redis.withJedisClient { implicit client =>
      Dress.up(client).get(Constants.USERNAME_UID.replaceAll("%s", email)) match { 
        case Some(uid) => false
        case None => {
          val nextUid = client.incr(Constants.GLOBAL_NEXTUID)
          client.set(Constants.UID_USERNAME.replaceAll("%s", nextUid.toString), email)
          client.set(Constants.UID_PASSWORD.replaceAll("%s", nextUid.toString), password)
          client.set(Constants.USERNAME_UID.replaceAll("%s", email), nextUid.toString)
          true
        }
      }
    } 
  }

  def parseSubscription(sub: String):String = {
    sub.split("|")(0)
  }

  def getSubscriptions(email: String): Set[String] = {
    redis.withJedisClient { implicit client =>
      val uid = client.get(Constants.USERNAME_UID.replaceAll("%s", email))
      val sids:Set[String] = client.smembers(uid).toSet
      sids map { sid =>
        val subscription = client.get(Constants.USERNAME_UID.replaceAll("$1", uid.toString).replaceAll("$2", sid.toString)) 
        parseSubscription(subscription)
      }
    }
  }
}
