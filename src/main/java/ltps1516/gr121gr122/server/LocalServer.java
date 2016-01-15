package ltps1516.gr121gr122.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Created by rob on 04-01-16.
 */
public class LocalServer {
    // Base URI the Grizzly HTTP server will listen on
    private final String BASE_URI = "http://localhost:8080/";

    // Custom JSON mapper
    private ObjectMapper mapper;

    // LocalServer resources
    private final ResourceConfig resourceConfig;

    public LocalServer() {
        // Create custom mapper
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // Create resources with packages and mapper
        resourceConfig = new ResourceConfig()
                .packages("ltps1516.gr121gr122.server")
                .register(new JacksonJaxbJsonProvider(mapper, Annotations.values()));
    }

    public void startServer() {
        // Start server with resources
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);

        // Register shutdownhook of server
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
    }
}
