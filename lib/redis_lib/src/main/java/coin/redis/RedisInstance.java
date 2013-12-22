package coin.redis;

import java.util.Map;
import java.util.Set;

import coin.redis.data.UserDBData;
import coin.redis.data.UserData;
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

    public ResponseData register(UserData data) {
        if (jedis.exists(Contant.QUERY_UID.replace(Contant.REPLACE, data.email))) {
            return new ResponseData(ResponseData.Code.FAILED, "user exists...");
        }

        long uid = jedis.incr(Contant.NEXT_ID);
        String uidStr = String.valueOf(uid);

        Pipeline pipeline = jedis.pipelined();
        // Register user info
        pipeline.set(Contant.QUERY_UID.replace(Contant.REPLACE, data.email), uidStr);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr), Contant.EMAIL, data.email);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr), Contant.PWD, data.pwd);
        pipeline.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uidStr),
                Contant.RULE.replace(Contant.REPLACE, data.rule_id), data.param);

        // Add uid to certain rule set
        pipeline.sadd(Contant.RULE_SET.replace(Contant.REPLACE, data.rule_id), uidStr);

        Response<String> returnUid = pipeline.get(Contant.QUERY_UID.replace(Contant.REPLACE, data.email));

        pipeline.sync();

        return new ResponseData(ResponseData.Code.SUCCESS, "successfully register to our system with uid: "
                + returnUid.get());
    }

    public ResponseData subscribe(UserData data) {
        if (data.rule_id == null || data.param == null) {
            return new ResponseData(ResponseData.Code.FAILED, "no rule and param...");
        }
        if (!jedis.exists(Contant.QUERY_UID.replace(Contant.REPLACE, data.email))) {
            return new ResponseData(ResponseData.Code.FAILED, "user not exists...");
        }

        String uid = jedis.get(Contant.QUERY_UID.replace(Contant.REPLACE, data.email));

        Transaction transaction = jedis.multi();

        String ruleId = String.valueOf(data.rule_id);
        transaction.hset(Contant.UID_PATTERN.replace(Contant.REPLACE, uid),
                Contant.RULE.replace(Contant.REPLACE, ruleId), data.param);
        transaction.sadd(Contant.RULE_SET.replace(Contant.REPLACE, ruleId), uid);

        transaction.exec();
        return new ResponseData(ResponseData.Code.SUCCESS, "Successfully subscribe new rules.");
    }

    public ResponseData unsubscribe(UserData data) {
        if (data.rule_id == null) {
            return new ResponseData(ResponseData.Code.FAILED, "no rule...");
        }
        if (!jedis.exists(Contant.QUERY_UID.replace(Contant.REPLACE, data.email))) {
            return new ResponseData(ResponseData.Code.FAILED, "user not exists...");
        }

        String uid = jedis.get(Contant.QUERY_UID.replace(Contant.REPLACE, data.email));

        Transaction transaction = jedis.multi();

        String ruleId = String.valueOf(data.rule_id);
        transaction.hdel(Contant.UID_PATTERN.replace(Contant.REPLACE, uid),
                Contant.RULE.replace(Contant.REPLACE, ruleId));
        transaction.srem(Contant.RULE_SET.replace(Contant.REPLACE, ruleId), uid);
        transaction.exec();
        return new ResponseData(ResponseData.Code.SUCCESS, "Successfully unsubscribe rules: " + ruleId);
    }

    public Set<String> getUids(String rule_id) {
        if (!jedis.exists(Contant.RULE_SET.replace(Contant.REPLACE, rule_id))) {
            return null;
        }
        return jedis.smembers(Contant.RULE_SET.replace(Contant.REPLACE, rule_id));
    }

    public UserDBData getUser(String uid) {
        if (!jedis.exists(Contant.UID_PATTERN.replace(Contant.REPLACE, uid))) {
            return null;
        }
        Map<String, String> info = jedis.hgetAll(Contant.UID_PATTERN.replace(Contant.REPLACE, uid));
        String email = info.get(Contant.EMAIL);
        String pwd = info.get(Contant.PWD);
        info.remove(Contant.EMAIL);
        info.remove(Contant.PWD);

        Map<String, String> rules = info;

        UserDBData data = new UserDBData(email, pwd, rules);
        return data;
    }

    public String getEmail(String uid) {
        return jedis.hget(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.EMAIL);
    }

    public String getParam(String uid) {
        return jedis.hget(Contant.UID_PATTERN.replace(Contant.REPLACE, uid), Contant.PARAM);
    }

}
