package ltps1516.gr121gr122.server;

import ltps1516.gr121gr122.model.user.NfcCard;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by rob on 04-01-16.
 */

@Path("{object}")
public class Resource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("object") String object) {
        System.out.println("Get request received for: " + object);

        return Response
                .accepted()
                .entity(new NfcCard())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void post(@PathParam("object") String object, String body) {
        System.out.println("Post request received for: " + object);
        System.out.println("With body: " + body);
    }

    @PATCH @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void patch(@PathParam("object") String object, @PathParam("id") String id, String body) {
        System.out.println("Patch request received for: " + object);
        System.out.println("With body: " + body);
        System.out.println("And ID: " + id);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("object") String object, @PathParam("id") String id) {
        System.out.println("Delete request received for: " + object);
        System.out.println("And ID: " + id);
    }
}
