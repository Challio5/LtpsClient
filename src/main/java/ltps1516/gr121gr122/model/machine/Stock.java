package ltps1516.gr121gr122.model.machine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import ltps1516.gr121gr122.model.Model;
import ltps1516.gr121gr122.model.user.Product;

/**
 * Created by rob on 01-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock implements Model {

    private IntegerProperty stockId;
    private IntegerProperty amount;
    private IntegerProperty locationX;
    private IntegerProperty locationY;
    private IntegerProperty productId;
    private ObjectProperty<Product> product;

    public Stock(
            @JsonProperty("stockId") int stockId,
            @JsonProperty("amount") int amount,
            @JsonProperty("locationX") int locationX,
            @JsonProperty("locationY") int locationY,
            @JsonProperty("productId") int productId,
            @JsonProperty("product") Product product) {

        this.stockId = new SimpleIntegerProperty(stockId);
        this.amount = new SimpleIntegerProperty(amount);
        this.locationX = new SimpleIntegerProperty(locationX);
        this.locationY = new SimpleIntegerProperty(locationY);
        this.productId = new SimpleIntegerProperty(productId);
        this.product = new SimpleObjectProperty<>(product);
    }

    // Test constructor
    public Stock() {
        this.amount = new SimpleIntegerProperty(100);
        this.locationX = new SimpleIntegerProperty(1);
        this.locationY = new SimpleIntegerProperty(2);
        this.productId = new SimpleIntegerProperty(1);
        this.product = new SimpleObjectProperty<>(new Product("DRANK"));
    }

    @Override
    @JsonProperty("stockId")
    public long getId() {
        return 0;
    }

    public int getStockId() {
        return stockId.get();
    }

    public IntegerProperty stockIdProperty() {
        return stockId;
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public int getLocationX() {
        return locationX.get();
    }

    public IntegerProperty locationXProperty() {
        return locationX;
    }

    public int getLocationY() {
        return locationY.get();
    }

    public IntegerProperty locationYProperty() {
        return locationY;
    }

    public int getProductId() {
        return productId.get();
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public Product getProduct() {
        return product.get();
    }

    public ObjectProperty<Product> productProperty() {
        return product;
    }

    // Setter


    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "amount=" + amount.get() +
                ", locationX=" + locationX.get() +
                ", locationY=" + locationY.get() +
                ", product=" + product.get() +
                '}';
    }
}
