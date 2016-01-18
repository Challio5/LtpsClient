package ltps1516.gr121gr122.control.main;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.control.comport.ComPort;
import ltps1516.gr121gr122.control.comport.Protocol;
import ltps1516.gr121gr122.control.converter.ProductConverter;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.machine.Stock;
import ltps1516.gr121gr122.model.user.Order;
import ltps1516.gr121gr122.model.user.OrderStatus;
import ltps1516.gr121gr122.model.user.Product;
import ltps1516.gr121gr122.model.user.ProductOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by rob on 05-01-16.
 */
public class OrderController {

    private Context context;
    private Logger logger;

    // Controller
    private ApiController apiController;

    // Root
    @FXML private GridPane root;

    // Order view
    @FXML private ListView<Order> orderListView;

    // Order table
    @FXML private TableView<ProductOrder> productOrderTable;

    @FXML private TableColumn<ProductOrder, Integer> amountColumn;
    @FXML private TableColumn<ProductOrder, Product> productColumn;
    @FXML private TableColumn<ProductOrder, Double> priceColumn;

    // Button
    @FXML private Button deleteButton;
    @FXML private Button collectButton;

    // FocusedNode
    private Node focusedNode;

    @FXML private void initialize() {
        logger = LogManager.getLogger(this.getClass().getName());

        // Get model instance
        context = Context.getInstance();

        // Controller to API
        apiController = new ApiController();

        orderListView.setCellFactory(this::listCellFactory);

        context.setSelectedOrderProperty(orderListView.getSelectionModel().selectedItemProperty());
        context.setSelectedProductOrderProperty(productOrderTable.getSelectionModel().selectedItemProperty());

        // Set tablecells to columns
        amountColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), 1, 2, 3, 4, 5, 6, 7, 8, 9));
        priceColumn.setCellFactory(this::tableCellFactory);
        productColumn.setCellFactory(TextFieldTableCell.forTableColumn(new ProductConverter()));

        // Set listcell to listview
        orderListView.setItems(context.getUser().getOrders());

        // Listen to orderselection
        orderListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            productOrderTable.setItems(newValue.getProductOrderList());
            if(ComPort.getInstance().getVendingSerialPort() != null) {
                if (newValue.getStatusId() != OrderStatus.ORDERED) {
                    collectButton.setDisable(true);
                } else {
                    collectButton.setDisable(false);
                }
            }
        });

        new Thread(this::initializeScene).start();
    }

    // New thread for binding to scene
    private void initializeScene() {
        while(root.getScene() == null);

        // Bind delete button
        deleteButton.disableProperty().bind(
                Bindings.notEqual(root.getScene().focusOwnerProperty(), productOrderTable).and(
                        Bindings.notEqual(root.getScene().focusOwnerProperty(), orderListView)).and(
                        Bindings.notEqual(root.getScene().focusOwnerProperty(), deleteButton)));

        root.getScene().focusOwnerProperty().addListener((observable, oldValue, newValue) -> {
            focusedNode = oldValue;
        });
    }

    // Collect products
    @FXML private boolean collect(ActionEvent event) {
        boolean success = false;

        int index = orderListView.getSelectionModel().getSelectedIndex();
        Order order = orderListView.getItems().get(index);

        if(order != null) {
            // Loop each productOrder
            loop:
            for(ProductOrder productOrder : order.getProductOrderList()) {
                    // Loop amount of stock in productOrder
                    for(int i = 0; i < productOrder.getAmount(); i++) {
                        // Get stock location
                        Stock stock = Context.getInstance().getMachine().getStockList().stream()
                                .filter(item -> item.getProduct().getId() == productOrder.getProduct().getId()).findAny().get();
                        byte[] location = {(byte) stock.getLocationX(), (byte) stock.getLocationY(), '.'};

                        // Send message to MCU
                        // Create wait message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Wait for product to be collected");

                        success = ComPort.getInstance().writeVendingMessage(Protocol.LOCATION, location);

                        if(success) {
                            alert.setContentText("Product successfully collected");
                            logger.info("Product successfully collected: " + productOrder.getProduct());
                            alert.showAndWait();
                        } else {
                            alert.setContentText("Product could not be collected");
                            logger.info("Collection of products not successfull");
                            break loop;
                        }
                    }
                }

            if(success) {
                logger.info("all orders collected");

                // ToDo collect order post
                order.setStatusId(OrderStatus.PAYDANDCOLLECTED);
                collectButton.setDisable(true);
            }
        }

        return success;
    }

    @FXML private void delete(ActionEvent event) {
        if (focusedNode != null) {
            if (focusedNode.equals(productOrderTable)) {
                ProductOrder productOrder = productOrderTable.getSelectionModel().getSelectedItem();

                logger.info("Do delete request for productOrder: " + productOrder.getProduct().getName());
                boolean success = apiController.getCrudController().delete(productOrder);

                logger.info("Delete request is: " + success);
                if(success) productOrderTable.getItems().remove(productOrder);

            } else if (focusedNode.equals(orderListView)) {
                Order order = orderListView.getSelectionModel().getSelectedItem();

                logger.info("Do delete request for order: " + order.getId());
                boolean success = apiController.getCrudController().delete(order);

                logger.info("Delete request is: " + success);
                if(success) orderListView.getItems().remove(order);

            } else {
                logger.warn("Focus on unrecognized node for deleting");
            }
        } else {
            logger.warn("No node previously focused for deleting");
        }

    }

    // Cell factory for displaying orders
    private ListCell<Order> listCellFactory(ListView<Order> param) {
        return new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    this.backgroundProperty().bind(Bindings.createObjectBinding(() ->
                            new Background(new BackgroundFill(
                                item.getStatusId().color(), CornerRadii.EMPTY, Insets.EMPTY)), item.statusIdProperty()));

                    textProperty().bind(Bindings.format(
                            "Order: %-20d€%.2f",
                            item.orderIdProperty(),
                            item.orderPriceProperty()));
                } else {
                    textProperty().unbind();
                    textProperty().set("");
                }
            }
        };
    }

    // Cell factory for displaying prices
    private TableCell<ProductOrder, Double> tableCellFactory(TableColumn<ProductOrder, Double> param) {
        return new TableCell<ProductOrder, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                if(item != null && !empty) {
                    this.setText(String.format("%.2f", item));
                } else {
                    this.setText("");
                }
            }
        };
    }
}
