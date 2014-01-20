package common

object Constants {
  val GLOBAL_NEXTUID = "global:nextuid"
  val GLOBAL_NEXTSID = "global:nextsid"
  val UID_USERNAME = "uid:%s:username"
  val UID_PASSWORD = "uid:%s:password"
  val UID_SUBSCRIPTIONS = "uid:%s:subscriptions"
  val UID_SID = "uid:%1:sid:%2"
  val USERNAME_UID = "username:%s:uid"
  
  val GLOBAL_NEXTRID = "global:nextrid"
  val RULE_SUPPORTED = "rules:supported"
  val RULE_DESCRIPTION = "rid:$1:description"
  val RULE_TEMPLATE = "rid:$1:template"
  val RULE_NAME = "rid:$1:rulename"
  val RULE_PRICES = "rid:%s"
  val RULE_PRICE_SET = "rid:%1:price:%2"
  val RULE_DEFAULT_ALERT = "rid:$1:alert"
  val RULE_BRIEF_DESCRIPTION = "rid:$1:brief_description"
  val RULE_RID = "rulename:$1:rid"
}
