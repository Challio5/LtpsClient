package ltps1516.gr121gr122.control.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.user.Order;
import ltps1516.gr121gr122.model.user.Product;
import ltps1516.gr121gr122.model.user.ProductOrder;
import ltps1516.gr121gr122.view.CustomLabel;
import ltps1516.gr121gr122.view.CustomNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by rob on 06-01-16.
 */

public class ProductController {
    // View
    @FXML private GridPane root;
    @FXML private ListView<ProductOrder> listView;
    @FXML private Pagination pagination;
    @FXML private Label totalPrice;
    @FXML private Button orderButton;

    // Logger
    private Logger logger;

    // Control
    private ObjectMapper mapper;
    private ApiController apiController;

    private int gridSize;

    private ObservableList<CustomLabel<Product>> labelList;

    @FXML private void initialize() {
        // Logger
        logger = LogManager.getLogger(this.getClass());

        // Create mapper
        mapper = new ObjectMapper();

        // Create observable property list
        ObservableList<ProductOrder>  orderList = FXCollections.observableArrayList(
                param -> new Observable[] {param.productProperty(), param.priceProperty(), param.amountProperty()});
        listView.setItems(orderList);

        // Bind totalprice
        DoubleBinding binding = Bindings.createDoubleBinding(() ->
                listView.getItems().stream().mapToDouble(ProductOrder::getPrice).sum(),
                    new SimpleListProperty<>(listView.getItems()));
        totalPrice.textProperty().bind(Bindings.format("Total: €%.2f", binding));

        // Initialize list with labels
        labelList = FXCollections.observableArrayList();

        // Set style
        listView.setStyle("-fx-font-family: monospace");

        // Build costum listcells
        listView.setCellFactory(this::listCellFactory);

        // Couple orderlist to context
        Context.getInstance().setOrderList(listView.getItems());

        // Drag feature listview
        listView.setOnDragOver(this::onDragOver);
        listView.setOnDragDropped(this::onDragDropped);

        // Get products from API and context
        apiController = new ApiController();
        apiController.getProducts();
        ObservableList<Product> productsList = Context.getInstance().getProductList();

        // Convert products to labels
        productsList.forEach(product -> {
            CustomLabel<Product> label = new CustomLabel<>(product);

            label.setOnMouseClicked(this::onMouseClicked);
            labelList.add(label);
        });

        // Create grid (2,1 / 2,2)
        gridSize = 2;
        IntStream.range(0, gridSize).forEach(j -> {
            if(j < productsList.size()) {
                Label label = labelList.get(j);
                root.add(label, 2, j + 1);
            }
        });

        // Set pagination
        pagination.setPageCount((int) (Math.ceil(productsList.size() / ((double) gridSize))));
        pagination.currentPageIndexProperty().addListener(this::paginaSelection);

        // Button disable
        orderButton.disableProperty().bind(new SimpleListProperty<>(listView.getItems()).sizeProperty().isEqualTo(0));
    }

    // Mouseclick
    @FXML private void onMouseClicked(MouseEvent event) {
        CustomLabel label = (CustomLabel) event.getSource();
        Product product = (Product) label.getModel();

        int index = IntStream.range(0, listView.getItems().size()).filter((i) ->
                product.getId() == listView.getItems().get(i).getProduct().getId()).findFirst().orElse(-1);

        this.checkIndex(product, index);
    }

    /* Drag and drop */

    // Deserialize productOrder when dropped
    @FXML private void onDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        if(dragboard.hasString()) {
            try {
                String json = dragboard.getString();
                Product product = mapper.readValue(json, Product.class);

                int index = IntStream.range(0, listView.getItems().size()).filter((i) ->
                        product.getId() == listView.getItems().get(i).getProduct().getId()).findFirst().orElse(-1);

                this.checkIndex(product, index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method that updates the list with the given index
    @FXML private void checkIndex(Product product, int index) {
        // Max of product
        if (index >= 0) {
            if(listView.getItems().get(index).getAmount() < 10) {
                listView.getItems().get(index).increment();
                logger.info("Product already in list, incremented with one");
            }
        } else {
            ProductOrder productOrder = new ProductOrder();
            productOrder.setProductId((int) product.getId());
            productOrder.setProduct(product);
            productOrder.setAmount(1);

            listView.getItems().add(productOrder);
            logger.info("Product not in list, added one");
        }
    }

    // Set transfermode as copy
    @FXML private void onDragOver(DragEvent event) {
        if(event.getGestureSource() instanceof Label) {
            event.acceptTransferModes(TransferMode.COPY);
        }

        event.consume();
    }

    // Listener for pagination
    public void paginaSelection(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        IntStream.range(0, gridSize).forEach(j -> {
            // Calculate indexes
            int oldIndex = gridSize * oldValue.intValue() + j;
            int newIndex = gridSize * newValue.intValue() + j;

            // Remove old label
            if(oldIndex < labelList.size()) {
                CustomLabel<Product> oldLabel = labelList.get(oldIndex);
                root.getChildren().remove(oldLabel);
            }

            // Add new label
            if(newIndex < labelList.size()) {
                CustomLabel<Product> newLabel = labelList.get(newIndex);
                root.add(newLabel, 2, j + 1);
            }
        });
    }

    @FXML
    @SuppressWarnings("unchecked")
    // Method to post an order and navigate to it
    private void order(ActionEvent event) {
        // Post new order
        long id = apiController.postProducts();

        // If id returned (Succesfull posted)
        if(id >= 0) {
            // Clear list with products
            this.clear(event);

            // Use navigation to go to orderoverview
            CustomNavigation navigation = (CustomNavigation) root.getScene().getRoot().getChildrenUnmodifiable().stream()
                    .filter(i -> i instanceof CustomNavigation).findAny().get();
            navigation.getSelectionModel().select(1);

            // Get created order in list from root
            GridPane pane = (GridPane) navigation.getSelectionModel().getSelectedItem();
            ListView<Order> listView = (ListView<Order>) pane.getChildren().stream()
                    .filter(i -> i instanceof ListView).findAny().get();
            Order order = listView.getItems().stream().filter(j -> j.getId() == id).findAny().get();

            // Select order and request focus
            listView.requestFocus();
            listView.getSelectionModel().select(order);
        }
    }

    @FXML private void clear(ActionEvent event) {
        listView.getItems().clear();
    }

    private ListCell<ProductOrder> listCellFactory(ListView<ProductOrder> param) {
        return new ListCell<ProductOrder>(){

            @Override
            protected void updateItem(ProductOrder productOrder, boolean bln) {
                super.updateItem(productOrder, bln);
                if (productOrder != null) {
                    textProperty().bind(Bindings.format(
                            "%-5d%-30s€%.2f",
                            productOrder.amountProperty(),
                            productOrder.getProduct().nameProperty(),
                            productOrder.priceProperty()));
                } else {
                    this.textProperty().unbind();
                    this.setText("");
                }
            }
        };
    }
}
