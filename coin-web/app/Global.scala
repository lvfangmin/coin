import play.api._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Data.init()
  }
}

object Data {

  def init() = {

  }
}
