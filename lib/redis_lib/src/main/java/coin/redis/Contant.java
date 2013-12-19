package coin.redis;

public class Contant {
    public final static String REPLACE = "$1";
    public final static String NEXT_ID = "global:nextUserId";

    public final static String QUERY_UID = "email:$1:uid";

    public final static String UID_PATTERN = "uid:$1";
    public final static String EMAIL = "email";
    public final static String PWD = "pwd";
    public final static String RULE = "rule_id:$1";
    public final static String PARAM = "param";

    public final static String RULE_SET = "global:rule:$1";
}
