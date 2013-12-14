package coin.redis;

public class RedisConf {
    private String redis_url;

    public RedisConf(String redis_url) {
        this.redis_url = redis_url;
    }

    public String getRedis_url() {
        return redis_url;
    }

    public void setRedis_url(String redis_url) {
        this.redis_url = redis_url;
    }

}
