package models

import play.Logger

import play.api.Play.current
import persist.Redis
import common.Constants
import scala.collection.JavaConversions._
import redis.clients.jedis.Transaction

object Subscription extends Redis {

  def subscribe(ruleId: String, username: String, price: String) = {
    redis.withJedisClient { implicit client =>
      val uid = client.get(Constants.USERNAME_UID.replaceAll("%s", username))
      val nextSid = client.incr(Constants.GLOBAL_NEXTSID) 
      val t = client.multi()
      t.set(Constants.UID_SID.replaceAll("%1", uid.toString).replaceAll("%2", nextSid.toString), ruleId + "|" + price)
      t.sadd(Constants.UID_SUBSCRIPTIONS.replaceAll("%s", uid), nextSid.toString)
      t.sadd(Constants.RULE_PRICE_SET.replaceAll("%1", ruleId).replaceAll("%2", price), uid + ":" + nextSid)
      t.sadd(Constants.RULE_PRICES.replaceAll("%s", ruleId), price)
      t.exec
      Logger.info("subscribe to rule {}", ruleId)
    }
  }

  def getSubscriptions(username: String):Set[(String, String, String, String)] = {
    redis.withJedisClient { implicit client =>
      val uid = client.get(Constants.USERNAME_UID.replaceAll("%s", username))
      val sids:Set[String] = client.smembers(Constants.UID_SUBSCRIPTIONS.replaceAll("%s", uid)).toSet

      sids map { sid =>
        val params = client.get(Constants.UID_SID.replaceAll("%1", uid).replaceAll("%2", sid)).split('|')
        (uid, sid, params(0), params(1))
      }
    }    
  }

  def delete(cond: (String, String, String, String)) = {
    redis.withJedisClient { implicit client =>
      val (uid, sid, rid, price) = cond
      val uidsidKey = Constants.UID_SID.replaceAll("%1", uid).replaceAll("%2", sid)

      val t = client.multi()
      t.del(uidsidKey)
      t.srem(Constants.UID_SUBSCRIPTIONS.replaceAll("%s", uid), sid)
      val priceKey = Constants.RULE_PRICE_SET.replaceAll("%1", rid).replaceAll("%2", price)
      t.srem(priceKey, uid + ":" + sid)
      if (t.scard(priceKey) == 0) {
        t.del(priceKey)
        t.srem(Constants.RULE_PRICES.replaceAll("%s", rid), price)
      }
      t.exec
    }
  }
}
