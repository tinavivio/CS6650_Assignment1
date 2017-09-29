package Server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/myresource")
public class MyResource {

    @GET
    @Produces("text/plain")
    public String getIt() {
        return "Hi there!";
    }

    @POST
    @Consumes("text/plain")
    @Produces("text/plain")
    public String postText(String content) {
        return "Content length: " + Integer.toString(content.length());
    }

}
