package coin.redis.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserData {
    public String email;
    public String pwd;
    public String rule_id;
    public String param;

    public UserData() {

    }

    public UserData(String email, String rule, String param) {
        this.email = email;
        this.rule_id = rule;
        this.param = param;
    }

    public UserData(String email, String pwd, String rule, String param) {
        this.email = email;
        this.pwd = pwd;
        this.rule_id = rule;
        this.param = param;
    }

    @Override
    public String toString() {
        return String.format("email: %s, pwd: %s, rule_id: %s, param: %s", email, pwd, rule_id, param);
    }

}
