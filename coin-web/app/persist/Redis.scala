package persist

import play.api.Play.current
import com.typesafe.plugin.RedisPlugin

trait Redis {
  val redis = current.plugin[RedisPlugin].get.sedisPool
}
