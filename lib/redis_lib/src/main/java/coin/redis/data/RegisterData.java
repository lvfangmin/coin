package coin.redis.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterData {
    public String email;
    public String pwd;
    public Integer rule;
    public String param;

    public RegisterData() {

    }

    public RegisterData(String email, int rule, String param) {
        this.email = email;
        this.rule = rule;
        this.param = param;
    }

    public RegisterData(String email, String pwd, int rule, String param) {
        this.email = email;
        this.pwd = pwd;
        this.rule = rule;
        this.param = param;
    }

    @Override
    public String toString() {
        return String.format("email: %s, pwd: %s, rule: %d, param: %s", email, pwd, rule, param);
    }

}
