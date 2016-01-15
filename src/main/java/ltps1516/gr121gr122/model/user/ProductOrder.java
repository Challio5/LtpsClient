package ltps1516.gr121gr122.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 02-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductOrder implements Model {
    private IntegerProperty productOrderId;
    private IntegerProperty orderId;

    private DoubleProperty price;
    private IntegerProperty amount;

    private IntegerProperty productId;
    private ObjectProperty<Product> product;

    public ProductOrder(
            @JsonProperty("productOrderId") int id,
            @JsonProperty("OrderId") int orderId,
            @JsonProperty("price") double price,
            @JsonProperty("amount") int amount,
            @JsonProperty("productId") int productId,
            @JsonProperty("product") Product product) {
        this.productOrderId = new SimpleIntegerProperty(id);
        this.orderId = new SimpleIntegerProperty(orderId);
        this.price = new SimpleDoubleProperty(price);
        this.amount = new SimpleIntegerProperty(amount);
        this.productId = new SimpleIntegerProperty(productId);
        this.product = new SimpleObjectProperty<>(product);

        // Bind price
        this.price.bind(Bindings.multiply(this.amount, this.product.get().priceProperty()));
    }

    // Test constructor
    public ProductOrder(String test) {
        this.productOrderId = new SimpleIntegerProperty(1);
        this.orderId = new SimpleIntegerProperty(1);
        this.price = new SimpleDoubleProperty(0.0);
        this.amount = new SimpleIntegerProperty(3);
        this.productId = new SimpleIntegerProperty(1);
        this.product = new SimpleObjectProperty<>(new Product("DRANK"));
    }

    public ProductOrder() {
        this.productOrderId = new SimpleIntegerProperty();
        this.orderId = new SimpleIntegerProperty();
        this.price = new SimpleDoubleProperty();
        this.amount = new SimpleIntegerProperty();
        this.productId = new SimpleIntegerProperty();
        this.product = new SimpleObjectProperty<>(new Product());
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "id=" + productOrderId +
                ", price=" + price +
                ", amount=" + amount +
                ", product=" + product +
                '}';
    }

    @Override
    @JsonProperty("productOrderId")
    public long getId() {
        return productOrderId.get();
    }

    public IntegerProperty idProperty() {
        return productOrderId;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public Product getProduct() {
        return product.get();
    }

    public ObjectProperty<Product> productProperty() {
        return product;
    }

    public int getProductOrderId() {
        return productOrderId.get();
    }

    public IntegerProperty productOrderIdProperty() {
        return productOrderId;
    }

    public int getOrderId() {
        return orderId.get();
    }

    public IntegerProperty orderIdProperty() {
        return orderId;
    }

    public int getProductId() {
        return productId.get();
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    // Setter
    public void setProductOrderId(int productOrderId) {
        this.productOrderId.set(productOrderId);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }
    public void setProduct(Product product) {
        this.product.set(product);
        this.price.bind(Bindings.multiply(this.amount, this.product.get().priceProperty()));
    }

    public void setOrderId(int orderId) {
        this.orderId.set(orderId);
    }

    // (De/In)crement
    public void increment() {
        int newValue = amount.get() + 1;
        amount.set(newValue);
    }

    public void decrement() {
        int newValue = amount.get() - 1;
        amount.set(newValue);
    }
}
