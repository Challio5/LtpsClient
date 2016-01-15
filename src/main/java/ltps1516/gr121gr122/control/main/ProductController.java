package ltps1516.gr121gr122.control.main;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ltps1516.gr121gr122.model.user.Product;
import ltps1516.gr121gr122.model.user.ProductOrder;
import ltps1516.gr121gr122.view.CustomLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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

    private int x;
    private int y;
    private int gridSize;

    private ObservableList<CustomLabel<Product>> labelList;

    public void initialize() {
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
            CustomLabel<Product> label = new CustomLabel<>(product,
                    product.getName() + "\n" + product.getDescription() + "\n" + product.getPrice());

            label.setOnMouseClicked(this::onMouseClicked);
            labelList.add(label);
        });

        // Get grid (last row = button, first column = selection)
        y = root.getRowConstraints().size();
        x = root.getColumnConstraints().size();
        gridSize = (x - 2) * (y - 3);

        // Set pagination
        pagination.setPageCount((int) (Math.ceil(productsList.size() / ((double) gridSize))));
        pagination.currentPageIndexProperty().addListener(this::paginaSelection);

        // Create grid
        IntStream.range(2, x).forEach(i -> {
            IntStream.range(1, y - 2).forEach(j -> {
                if(i + j - 3 < productsList.size()) {
                    Label label = labelList.get(i + j - 3);
                    root.add(label, i, j);
                }
            });
        });

        // Button disable
        orderButton.disableProperty().bind(new SimpleListProperty<>(listView.getItems()).sizeProperty().isEqualTo(0));
    }

    // Mouseclick
    private void onMouseClicked(MouseEvent event) {
        CustomLabel label = (CustomLabel) event.getSource();
        Product product = (Product) label.getModel();

        int index = IntStream.range(0, listView.getItems().size()).filter((i) ->
                product.getId() == listView.getItems().get(i).getProduct().getId()).findFirst().orElse(-1);

        if(index >= 0) {
            listView.getItems().get(index).increment();
            logger.info("Product already in list, incremented with one");
        } else {
            ProductOrder productOrder = new ProductOrder();
            productOrder.setProductId((int) product.getId());
            productOrder.setProduct(product);
            productOrder.setAmount(1);

            listView.getItems().add(productOrder);
            logger.info("Product not in list, added one");
        }
    }

    /* Drag and drop */

    // Deserialize productOrder when dropped
    private void onDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        if(dragboard.hasString()) {
            try {
                String json = dragboard.getString();
                Product product = mapper.readValue(json, Product.class);

                int index = IntStream.range(0, listView.getItems().size()).filter((i) ->
                        product.getId() == listView.getItems().get(i).getProduct().getId()).findFirst().orElse(-1);

                if(index >= 0) {
                    listView.getItems().get(index).increment();
                    logger.info("Product already in list, incremented with one");
                } else {
                    ProductOrder productOrder = new ProductOrder();
                    productOrder.setProductId((int) product.getId());
                    productOrder.setProduct(product);
                    productOrder.setAmount(1);

                    listView.getItems().add(productOrder);
                    logger.info("Product not in list, added one");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Set transfermode as copy
    private void onDragOver(DragEvent event) {
        if(event.getGestureSource() instanceof Label) {
            event.acceptTransferModes(TransferMode.COPY);
        }

        event.consume();
    }

    // Listener for pagination
    public void paginaSelection(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        IntStream.range(2, x).forEach(i -> {
            IntStream.range(1, y - 2).forEach(j -> {
                // Calculate indexes
                int oldIndex = gridSize * oldValue.intValue() + i + j - 3;
                int newIndex = gridSize * newValue.intValue() + i + j - 3;

                // Remove old label
                if(oldIndex < labelList.size()) {
                    CustomLabel<Product> oldLabel = labelList.get(oldIndex);
                    root.getChildren().remove(oldLabel);
                }

                // Add new label
                if(newIndex < labelList.size()) {
                    CustomLabel<Product> newLabel = labelList.get(newIndex);
                    root.add(newLabel, i, j);
                }
            });
        });
    }

    @FXML
    private void order(ActionEvent event) {
        apiController.postProducts();
        this.clear(event);
    }

    @FXML
    private void clear(ActionEvent event) {
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
