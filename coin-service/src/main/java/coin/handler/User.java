package coin.handler;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import coin.redis.RedisInstance;
import coin.redis.data.RegisterData;
import coin.redis.data.ResponseData;

@Path("user")
public class User {

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseData register(RegisterData registerData) {
        // TODO input validation
        return RedisInstance.getInstance().register(registerData);

    }

    @Path("/subscribe")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseData subscribe(RegisterData registerData) {
        // TODO input validation
        return RedisInstance.getInstance().subscribe(registerData);

    }

    @Path("/query/{uid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RegisterData query(@PathParam("uid") String uid) {
        // TODO input validation
        return RedisInstance.getInstance().query(uid);
    }
}
