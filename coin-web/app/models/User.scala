package models

import play.api.cache._
import play.api.Play.current

case class User(email: String, password: String)

object User {

  def authenticate(email: String, password: String):Boolean = {
    email match {
      case "l@y.com" => true
      case _ => false
    }
  }

  def findByEmail(email: String): Option[User] = {
    Cache.get(email)
    Some(User("l@y.com", "123"))
  }
}
