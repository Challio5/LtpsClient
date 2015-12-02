package ltps1516.gr121gr122.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ltps1516.gr121gr122.model.Order;
import ltps1516.gr121gr122.model.User;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by rob on 02-12-15.
 */
public class ApiController {

    // Fields
    private ObjectMapper mapper;
    private RestTemplate template;

    private String host;
    private int port;

    // Constructor
    public ApiController() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));

        template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));

        // Properties
        try(FileInputStream stream = new FileInputStream(Apl.class.getResource("/server.properties").getPath())) {
            Properties properties = new Properties();
            properties.load(stream);

            host = properties.getProperty("hostname");
            port = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Login method
    public void login(String username, String password) {
        String url = host + ":" + port + "/user/search/findByUsernameAndPassword?user_name=" + username +
                "&password=" + password;

        ObjectNode objectNode = template.getForObject(url, ObjectNode.class);

        try {
            JsonNode userNode = objectNode.get("_embedded").get("user").get(0);
            User user = mapper.treeToValue(userNode, User.class);

            JsonNode productNode = objectNode.get("_embedded").get("user").get(0).get("_embedded").get("orders");
            Order[] orders = mapper.treeToValue(productNode, Order[].class);
            user.setOrders(new ArrayList<>(Arrays.asList(orders)));

            System.out.println(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
