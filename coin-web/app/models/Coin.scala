package models

case class Coin(cointype: String)

object Coin {

  def isValid(cointype: String):Boolean = {
    cointype match {
      case "LTC" => true
      case _ => false
    } 
  }
}
