package coin.database;

import coin.Contant;
import coin.data.RegisterData;
import coin.data.ResponseData;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class RedisInstance {

    private static RedisInstance instance;

    private Jedis jedis;

    private RedisInstance() {
        jedis = new Jedis("localhost");
    }

    public static RedisInstance getInstance() {
        if (instance == null) {
            instance = new RedisInstance();
        }
        return instance;
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public ResponseData register(RegisterData data) {
        if (jedis.exists(Contant.QUERY_UID.replace(Contant.REPLACE, data.email))) {
            return new ResponseData(ResponseData.Code.FAILED, "user exists...");
        }

        long uid = jedis.incr(Contant.NEXT_ID);
        String uidStr = String.valueOf(uid);
        String ruleId = String.valueOf(data.rule);

        Pipeline pipeline = jedis.pipelined();
        // Register user info
        pipeline.set(Contant.QUERY_UID.replace(Contant.REPLACE, data.email), String.valueOf(uid));
        pipeline.set(Contant.EMAIL.replace(Contant.REPLACE, uidStr), data.email);
        pipeline.set(Contant.PWD.replace(Contant.REPLACE, uidStr), data.pwd);
        pipeline.set(Contant.RULE.replace(Contant.REPLACE, uidStr), ruleId);
        pipeline.set(Contant.PARAM.replace(Contant.REPLACE, uidStr), data.param);

        // Add uid to certain rule set
        pipeline.sadd(Contant.RULE_SET.replace(Contant.REPLACE, ruleId), uidStr);

        Response<String> returnUid = pipeline.get(Contant.QUERY_UID.replace(Contant.REPLACE, data.email));

        pipeline.sync();

        return new ResponseData(ResponseData.Code.SUCCESS, "successfully register to our system with uid: "
                + returnUid.get());
    }

    public ResponseData subscribe(RegisterData data) {
        if (data.rule == null && data.param == null) {
            return new ResponseData(ResponseData.Code.FAILED, "no rule or param...");
        }
        if (!jedis.exists(Contant.QUERY_UID.replace(Contant.REPLACE, data.email))) {
            return new ResponseData(ResponseData.Code.FAILED, "user not exists...");
        }

        String uid = jedis.get(Contant.QUERY_UID.replace(Contant.REPLACE, data.email));
        String preRuleId = jedis.get(Contant.RULE.replace(Contant.REPLACE, uid));
        String preParam = jedis.get(Contant.PARAM.replace(Contant.REPLACE, uid));

        Transaction transaction = jedis.multi();
        if (data.rule != null) {
            String ruleId = String.valueOf(data.rule);
            if (!ruleId.equals(preRuleId)) {
                transaction.set(Contant.RULE.replace(Contant.REPLACE, uid), ruleId);
                transaction.sadd(Contant.RULE_SET.replace(Contant.REPLACE, ruleId), uid);
                transaction.srem(Contant.RULE_SET.replace(Contant.REPLACE, preRuleId), uid);
            }
        }

        if (data.param != null) {
            String param = data.param;
            if (!param.equals(preParam)) {
                transaction.set(Contant.PARAM.replace(Contant.REPLACE, uid), param);
            }
        }
        transaction.exec();
        return new ResponseData(ResponseData.Code.SUCCESS, "Successfully subscribe new rules.");
    }

    public RegisterData query(String uid) {
        if (!jedis.exists(Contant.EMAIL.replace(Contant.REPLACE, uid))) {
            return null;
        }

        Pipeline pipeline = jedis.pipelined();

        Response<String> email = pipeline.get(Contant.EMAIL.replace(Contant.REPLACE, uid));
        Response<String> rule = pipeline.get(Contant.RULE.replace(Contant.REPLACE, uid));
        Response<String> param = pipeline.get(Contant.PARAM.replace(Contant.REPLACE, uid));

        pipeline.sync();

        RegisterData data = new RegisterData(email.get(), Integer.parseInt(rule.get()), param.get());
        return data;
    }
}
