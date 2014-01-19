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

object Admin extends Controller with Secured with Redis {
}
