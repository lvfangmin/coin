package coin.redis.data;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import coin.redis.Contant;

@XmlRootElement
public class UserDBData {
    public String email;
    public String pwd;
    public Map<String, String> rules;

    public UserDBData() {

    }

    public UserDBData(String email) {
        this.email = email;
    }

    public UserDBData(String email, String pwd, Map<String, String> rules) {
        this.email = email;
        this.pwd = pwd;
        this.rules = rules;
    }

    public String getParam(String rule_id) {
        return rules.get(Contant.RULE.replace(Contant.REPLACE, rule_id));
    }

    @Override
    public String toString() {
        return String.format("email: %s, pwd: %s", email, pwd);
    }
}
