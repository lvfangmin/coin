package coin.redis;

import java.util.Map;
import java.util.Set;

import coin.redis.data.RegisterData;
import coin.redis.data.ResponseData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class RedisInstance {
    private static RedisInstance instance;
    private static RedisConf conf;

    private Jedis jedis;

    private RedisInstance() {
        jedis = new Jedis(conf.getRedis_url());
    }

    public static RedisInstance getInstance() {
        if (instance == null && conf != null) {
            instance = new RedisInstance();
        }
        return instance;
    }

    public static void init(RedisConf conf) {
        RedisInstance.conf = conf;
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
        pipeline.set(Contant.QUERY_UID.replace(Contant.REPLACE, data.email), uidStr);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr), Contant.EMAIL, data.email);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr), Contant.PWD, data.pwd);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr), Contant.RULE, ruleId);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr), Contant.PARAM, data.param);

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
        String preRuleId = jedis.hget(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.RULE);
        String preParam = jedis.hget(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.PARAM);

        Transaction transaction = jedis.multi();
        if (data.rule != null) {
            String ruleId = String.valueOf(data.rule);
            if (!ruleId.equals(preRuleId)) {
                transaction.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.RULE, ruleId);
                transaction.sadd(Contant.RULE_SET.replace(Contant.REPLACE, ruleId), uid);
                transaction.srem(Contant.RULE_SET.replace(Contant.REPLACE, preRuleId), uid);
            }
        }

        if (data.param != null) {
            String param = data.param;
            if (!param.equals(preParam)) {
                transaction.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.PARAM, param);
            }
        }
        transaction.exec();
        return new ResponseData(ResponseData.Code.SUCCESS, "Successfully subscribe new rules.");
    }

    public RegisterData query(String uid) {
        if (!jedis.exists(Contant.EMAIL.replace(Contant.REPLACE, uid))) {
            return null;
        }

        Map<String, String> info = jedis.hgetAll(Contant.UID_PATTERN.replace(Contant.REPLACE, uid));

        RegisterData data = new RegisterData(info.get(Contant.EMAIL), Integer.parseInt(info.get(Contant.RULE)),
                info.get(Contant.PARAM));
        return data;
    }

    public Set<String> getUids(String rule_id) {
        if (!jedis.exists(Contant.RULE_SET.replace(Contant.REPLACE, rule_id))) {
            return null;
        }
        return jedis.smembers(Contant.RULE_SET.replace(Contant.REPLACE, rule_id));
    }

    public String getEmail(String uid) {
        return jedis.hget(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.EMAIL);
    }

    public String getParam(String uid) {
        return jedis.hget(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.PARAM);
    }

}
