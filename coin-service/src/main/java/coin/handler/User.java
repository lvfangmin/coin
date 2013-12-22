package coin.handler;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import coin.redis.RedisInstance;
import coin.redis.data.ResponseData;
import coin.redis.data.UserDBData;
import coin.redis.data.UserData;

@Path("user")
public class User {

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseData register(UserData registerData) {
        // TODO input validation
        return RedisInstance.getInstance().register(registerData);

    }

    @Path("/subscribe")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseData subscribe(UserData registerData) {
        // TODO input validation
        return RedisInstance.getInstance().subscribe(registerData);

    }
    
    @Path("/unsubscribe")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseData unsubscribe(UserData registerData) {
        // TODO input validation
        return RedisInstance.getInstance().unsubscribe(registerData);

    }

    @Path("/query/{uid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserDBData query(@PathParam("uid") String uid) {
        // TODO input validation
        return RedisInstance.getInstance().getUser(uid);
    }
}
