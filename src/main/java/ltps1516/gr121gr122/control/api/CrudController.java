package ltps1516.gr121gr122.control.api;


import ltps1516.gr121gr122.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by rob on 04-01-16.
 */
public class CrudController {

    private Logger logger;
    private WebTarget webTarget;

    // Constructor
    public CrudController(WebTarget webTarget) {
        this.logger = LogManager.getLogger(this.getClass().getName());
        this.webTarget = webTarget;
    }

    // Test Constructor
    public CrudController() {
        this.logger = LogManager.getLogger(this.getClass().getName());

        this.webTarget = ClientBuilder.newClient()
                .register(JacksonFeature.class)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .target("http://localhost:8080/");
    }

    // Parse name with class (Reflection)
    private String parseName(Class object) {
        String objectName = object.getSimpleName();

        if(object.isArray()) objectName =
                objectName.substring(0, objectName.length() - 2);

        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";

        return objectName.replaceAll(regex, replacement).toLowerCase();
    }

    public Model create(Model object) {
        logger.info("Create-Request for: " + object);

        Model model = null;
        try {
            Response response = webTarget
                    .path(parseName(object.getClass()))
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(object, MediaType.APPLICATION_JSON));

            int statusCode = response.getStatus();
            switch (Response.Status.fromStatusCode(statusCode)) {
                case CREATED:
                    logger.info("Post request accepted and model returned");
                    model = response.readEntity(object.getClass());
                    break;
                case CONFLICT:
                    logger.warn("Post request conflicted, id already exists");
                    break;
            }

            logger.info("Response of server: " + response.getStatusInfo());
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }

        return model;
    }

    public Model read(Class<? extends Model> object, long id) {
        logger.info("Read-request for: " + object.getSimpleName());

        Model model = null;

        try {
            model = webTarget
                    .path(parseName(object) + "/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get(object);

            logger.info("Response of server: " + model);
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }

        return model;
    }

    public Model[] readAll(Class<? extends Model[]> object) {
        logger.info("Read-request for: " + object.getSimpleName());

        Model[] model = null;

        try {
            model = webTarget
                    .path(parseName(object))
                    .request(MediaType.APPLICATION_JSON)
                    .get(object);

            String response = Arrays.stream(model).map(Object::toString).collect(Collectors.joining(" "));
            logger.info("Response of server: " + response);
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }

        return model;
    }

    public boolean update(Model object) {
        String path = parseName(object.getClass()) + "/" + object.getId();
        logger.info("Update-request on path " + path + " for: " + object);

        try {
            Response response = webTarget
                    .path(path)
                    .request()
                    .method("PATCH", Entity.entity(object, MediaType.APPLICATION_JSON));

            logger.info("Response of server: " + response.getStatusInfo());
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }

        return true;
    }

    public boolean delete(Model object) {
        logger.info("Delete-request of: " + object);

        try {
            Response response = webTarget
                    .path(parseName(object.getClass()) + "/" + object.getId())
                    .request()
                    .delete();

            logger.info("Response of server: " + response.getStatusInfo());
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }

        return true;
    }
}
