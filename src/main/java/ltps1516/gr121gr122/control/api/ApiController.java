package ltps1516.gr121gr122.control.api;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.Error;
import ltps1516.gr121gr122.model.machine.Machine;
import ltps1516.gr121gr122.model.machine.Stock;
import ltps1516.gr121gr122.model.user.Order;
import ltps1516.gr121gr122.model.user.Product;
import ltps1516.gr121gr122.model.user.ProductOrder;
import ltps1516.gr121gr122.model.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

/**
 * Created by rob on 02-12-15.
 */
public class ApiController {

    // Model
    private Context context;

    // CrudController
    private CrudController crudController;

    // Fields
    private ClientBuilder client;
    private WebTarget authWebTarget;

    // Properties
    private final String host;
    private final String port;

    // Logger
    private Logger logger;

        // Test
        public ApiController(String test) {
            host = "";
            port = "";

            crudController = new CrudController();
        }

        public CrudController getCrudController() {
            return crudController;
        }

    // Constructor
    public ApiController() {
        // Create logger
        logger = LogManager.getLogger(this.getClass().getName());

        // Get model from context
        context = Context.getInstance();

        // Properties
        host = Context.getInstance().getProperties().getProperty("hostname");
        port = Context.getInstance().getProperties().getProperty("port");
        String username = Context.getInstance().getProperties().getProperty("username");
        String password = Context.getInstance().getProperties().getProperty("password");

        // Setup client
        client = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);

        authWebTarget = client.build()
                .register(HttpAuthenticationFeature.basic(username, password))
                .target(host + ":" + port);

        // Create controller for CRUD operations
        crudController = new CrudController(authWebTarget);
    }

    // Server check
    public boolean serverCheck() {
        boolean success = false;

        try {
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));

            //System.out.println(proxy.address());
            new Socket(host, Integer.parseInt(port));
            success = true;
        }
        catch (UnknownHostException e) {
            logger.warn("Server not reachable");
        }
        catch (IOException e) {
            logger.warn(e.getMessage());
        }

        return success;
    }

    public void getMachineData (int machineId) throws WebApplicationException {
        logger.info("Getting machine data with machineId: " + machineId);

        try {
            // Get machine
            Machine machine = (Machine) crudController.read(Machine.class, machineId);
            logger.info("Response with machine data: " + machine);
            Context.getInstance().setMachine(machine);
        } catch (ResponseProcessingException e) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }

    // Login method
    public void login (String username, String password) throws WebApplicationException {
        // Get user from Api
        logger.info("Getting user data for user " + username);

        // Do request
        User user = client.build()
                .register(HttpAuthenticationFeature.basic(username, password))
                .target(host + ":" + port)
                .path("search/user/currentUser")
                .request(MediaType.APPLICATION_JSON)
                .get(User.class);

        logger.info("Response with user data " + user);

        // Add listeners for each order
        user.getOrders().forEach(order -> {
            // Listener for checking orderstatus (collect)
            order.statusIdProperty().addListener(this::statusListener);
            order.getProductOrderList()
                    .forEach(productOrder -> productOrder.amountProperty().addListener(this::amountListener));

            /*
            // Get disabled property
            Response orderCheck = authWebTarget
                    .path("order/collect_order")
                    .queryParam("orderId", order.getId())
                    .queryParam("machineId", context.getMachine().getId())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Check if collectable
            if(orderCheck.getLength() > 0) order.setError(new Error());
            */
        });

        // Update context
        context.setUser(user);
    }

    // Method to get products from API
    public void getProducts() {
        try {
            // Read products from API
            Product[] array = (Product[]) crudController.readAll(Product[].class);

            // Make list and add to context
            ObservableList<Product> products = FXCollections.observableArrayList(array);
            Context.getInstance().setProductList(products);
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }
    }

    // Method to post products to API
    public long postProducts() {
        logger.info("Create order on server");

        // Create JSON for posting order
        String json = "{\"userId\": " + Context.getInstance().getUser().getId() + "}";
        ObservableList<ProductOrder> products = Context.getInstance().getOrderList();

        // Order to be created
        Order order = null;

        // Post order with productOrders
        try {
            // Post order
            Response response = authWebTarget
                    .path("order")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));

            order = response.readEntity(Order.class);
            logger.info("Order posted: " + order);

            // Post productOrders with id fro order
            final Order finalOrder = order;
            IntStream.range(0, products.size()).forEach(i -> {
                // Set orderId of productOrder
                ProductOrder productOrder = products.get(i);
                productOrder.setOrderId((int) finalOrder.getId());

                // Post productOrder
                ProductOrder postedProductOrder = (ProductOrder) crudController.create(productOrder);
                logger.info("ProductOrder posted: " + postedProductOrder);

                finalOrder.getProductOrderList().add(postedProductOrder);
            });

            // Set model
            //order.setOrderPrice(order.getProductOrderList().stream().mapToDouble(ProductOrder::getPrice).sum());
            Context.getInstance().getUser().getOrders().add(order);
            logger.info("Order added to context: " + order);
        } catch (WebApplicationException e) {
            logger.warn(e.getMessage());
        }

        if(order != null) {
            return order.getId();
        } else {
            return -1;
        }
    }

    // Statuslistener (update order status to collected in API)
    public void statusListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue != null) {
            try {
                // Url for patching order
                logger.info("Patch order " + context.getSelectedOrder().getId());

                // Patch order and get updated stock
                Stock[] stock = authWebTarget
                        .path("order/collect_order")
                        .queryParam("orderId", context.getSelectedOrder().getId())
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity("", MediaType.TEXT_PLAIN), Stock[].class);

                logger.info("New stock data received: " + stock);

                // Update stock
                ObservableList<Stock> stockList = FXCollections.observableArrayList(stock);
                stockList.forEach((newStock) ->
                        context.getMachine().getStockList().forEach((oldStock) -> {
                            if (oldStock.getProductId() == newStock.getProductId())
                                oldStock.setAmount(newStock.getAmount());
                        })
                );

                // Update user/order
                context.getSelectedOrder().setError(new Error());
                context.getUser().setBalance(
                        context.getUser().getBalance() -
                                context.getSelectedOrder().getOrderPrice());
            } catch (WebApplicationException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    // ToDo check new amount with stock (Dis/En able collect button)
    // Amount listener (update productOrder amount with API)
    public void amountListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        try {
            logger.info("Patch productOrder " + context.getSelectedProductOrder().getId()
                    + " with new amount: " + context.getSelectedProductOrder().getAmount());

            crudController.update(context.getSelectedProductOrder());
            /*
            authWebTarget
                    .path("product_order/" + context.getSelectedProductOrder().getId())
                    .request()
                    .method("PATCH", Entity.entity(context.getSelectedProductOrder(), MediaType.APPLICATION_JSON));
                    */
        } catch(WebApplicationException e) {
            logger.warn(e.getMessage());
        }
    }
}
