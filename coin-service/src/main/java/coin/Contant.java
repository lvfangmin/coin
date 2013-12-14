package coin;

public class Contant {
    public final static String REPLACE = "$1";
    public final static String NEXT_ID = "global:nextUserId";

    public final static String QUERY_UID = "email:$1:uid";

    public final static String UID_PATTERN = "uid:$1";
    public final static String EMAIL = UID_PATTERN + ":email";
    public final static String PWD = UID_PATTERN + ":pwd";
    public final static String RULE = UID_PATTERN + ":rule";
    public final static String PARAM = UID_PATTERN + ":param";
    
    public final static String RULE_SET = "global:rule:$1";
}
