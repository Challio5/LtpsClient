package ltps1516.gr121gr122.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 01-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Model {
    private IntegerProperty productId;
    private StringProperty name;
    private DoubleProperty price;
    private StringProperty description;

    public Product(
            @JsonProperty("productId") int productId,
            @JsonProperty("name") String name,
            @JsonProperty("price") double price,
            @JsonProperty("description") String description) {
        this.productId = new SimpleIntegerProperty(productId);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.description = new SimpleStringProperty(description);
    }

    // Testconstructor
    public Product(String name) {
        this.productId = new SimpleIntegerProperty(1);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(0.0);
        this.description = new SimpleStringProperty("Lekkah");
    }

    public Product() {
        this.productId = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.description = new SimpleStringProperty();
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name=" + name +
                ", price=" + price +
                ", description=" + description +
                '}';
    }

    @Override
    @JsonProperty("productId")
    public long getId() {
        return productId.get();
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
