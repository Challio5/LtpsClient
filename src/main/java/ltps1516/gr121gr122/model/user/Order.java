package ltps1516.gr121gr122.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.Model;


/**
 * Created by rob on 01-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Model {
    private IntegerProperty orderId;
    private DoubleProperty orderPrice;

    @JsonIgnore
    private ObjectProperty<OrderStatus> statusId;

    @JsonIgnore
    private ListProperty<ProductOrder> productOrderList;

    public Order(
            @JsonProperty("orderId") int orderId,
            @JsonProperty("statusId") int statusId,
            @JsonProperty("orderPrice") double orderPrice,
            @JsonProperty("productOrders") ProductOrder[] productOrders
    ) {
        // Read properties
        this.orderId = new SimpleIntegerProperty(orderId);

        this.orderPrice = new SimpleDoubleProperty(orderPrice);

        // Create list and add productOrders
        this.productOrderList = new SimpleListProperty<>(
                FXCollections.observableArrayList((productOrder) -> new Observable[] {productOrder.amountProperty()}));
        this.productOrderList.addAll(productOrders);
        //Collections.sort(productOrderList, (item1, item2) -> item1.getId() > item2.getId()?(int)item2.getId():(int)item1.getId());

        // Bind total price of order
        this.orderPrice.bind(Bindings.createDoubleBinding(() ->
                this.productOrderList.stream().mapToDouble(ProductOrder::getPrice).sum(), productOrderList));

        this.statusId = new SimpleObjectProperty<>(OrderStatus.convert(statusId - 1));
    }

    // Test constructor
    public Order(String test) {
        this.orderId = new SimpleIntegerProperty(1);
        this.statusId = new SimpleObjectProperty<>(OrderStatus.PAYD);
        this.orderPrice = new SimpleDoubleProperty(0.0);

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
                '}';
    }

    public void stockAndBalanceCheck() {
        if(this.statusId.get() != OrderStatus.PAYDANDCOLLECTED) {
            // Stock check
            boolean outOfStock = !this.getProductOrderList().stream().allMatch(productOrder ->
                    Context.getInstance().getMachine().getStockList().stream().anyMatch(stock ->
                            stock.getProduct().getId() == productOrder.getProduct().getId() &&
                                    productOrder.getAmount() <= stock.getAmount()
                    ));

            // Balance check (Overrules stock check)
            boolean outOfBalance = this.getOrderPrice() > Context.getInstance().getUser().getBalance();

            if (outOfBalance) {
                this.statusId.set(OrderStatus.OUTOFBALANCE);
            } else if (outOfStock) {
                this.statusId.set(OrderStatus.OUTOFSTOCK);
            }
        }
    }

    // Getters
    @Override
    @JsonProperty("orderId")
    public long getId() {
        return orderId.get();
    }

    @JsonProperty("status")
    public int getStatus() {
        return statusId.get().ordinal() + 1;
    }

    public IntegerProperty orderIdProperty() {
        return orderId;
    }

    @JsonIgnore
    public OrderStatus getStatusId() {
        return statusId.get();
    }

    public ObjectProperty<OrderStatus> statusIdProperty() {
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

    // Setters
    public void setProductOrderList(ObservableList<ProductOrder> productOrderList) {
        this.productOrderList.set(productOrderList);
    }

    public void setStatusId(OrderStatus statusId) {
        this.statusId.set(statusId);
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice.set(orderPrice);
    }
}
