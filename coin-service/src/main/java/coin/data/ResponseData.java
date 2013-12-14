package coin.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponseData {
    public int code;
    public String msg;

    public ResponseData() {

    }

    public ResponseData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public interface Code {
        public int SUCCESS = 0;
        public int FAILED = -1;
    }
}
