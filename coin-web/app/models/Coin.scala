package models

case class Coin(cointype: String)

object Coin {

  def isValid(cointype: String):Boolean = {
    true
  }
}
