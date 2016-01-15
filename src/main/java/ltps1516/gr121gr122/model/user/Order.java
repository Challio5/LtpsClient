package ltps1516.gr121gr122.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ltps1516.gr121gr122.model.Error;
import ltps1516.gr121gr122.model.Model;


/**
 * Created by rob on 01-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Model {
    private IntegerProperty orderId;
    private IntegerProperty statusId;
    private DoubleProperty orderPrice;

    @JsonIgnore
    private ListProperty<ProductOrder> productOrderList;

    @JsonIgnore
    private ObjectProperty<Error> error;

    public Order(
            @JsonProperty("orderId") int orderId,
            @JsonProperty("statusId") int statusId,
            @JsonProperty("orderPrice") double orderPrice,
            @JsonProperty("productOrders") ProductOrder[] productOrders
    ) {
        // Read properties
        this.orderId = new SimpleIntegerProperty(orderId);
        this.statusId = new SimpleIntegerProperty(statusId);
        this.orderPrice = new SimpleDoubleProperty(orderPrice);

        // Create list and add productOrders
        this.productOrderList = new SimpleListProperty<>(
                FXCollections.observableArrayList((productOrder) -> new Observable[] {productOrder.amountProperty()}));
        this.productOrderList.addAll(productOrders);

        // Bind total price of order
        this.orderPrice.bind(Bindings.createDoubleBinding(() ->
                this.productOrderList.stream().mapToDouble(ProductOrder::getPrice).sum(), productOrderList));

        //
        this.error = new SimpleObjectProperty<>();
    }

    // Test constructor
    public Order(String test) {
        this.orderId = new SimpleIntegerProperty(1);
        this.statusId = new SimpleIntegerProperty(3);
        this.orderPrice = new SimpleDoubleProperty(0.0);

        this.error = new SimpleObjectProperty<>();

        ProductOrder productOrder = new ProductOrder("");
        productOrderList = new SimpleListProperty<>(FXCollections.observableArrayList(productOrder));
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", statusId=" + statusId +
                ", orderPrice=" + orderPrice +
                ", productOrderList=" + productOrderList +
                ", error=" + error +
                '}';
    }

    // Getters
    @Override
    @JsonProperty("orderId")
    public long getId() {
        return orderId.get();
    }

    public IntegerProperty orderIdProperty() {
        return orderId;
    }

    public int getStatusId() {
        return statusId.get();
    }

    public IntegerProperty statusIdProperty() {
        return statusId;
    }

    public double getOrderPrice() {
        return orderPrice.get();
    }

    public DoubleProperty orderPriceProperty() {
        return orderPrice;
    }

    public ObservableList<ProductOrder> getProductOrderList() {
        return productOrderList;
    }

    public Error getError() {
        return error.get();
    }

    public ObjectProperty<Error> errorProperty() {
        return error;
    }

    // Setters
    public void setProductOrderList(ObservableList<ProductOrder> productOrderList) {
        this.productOrderList.set(productOrderList);
    }

    public void setError(Error error) {
        this.error.set(error);
    }

    public void setStatusId(int statusId) {
        this.statusId.set(statusId);
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice.set(orderPrice);
    }
}
