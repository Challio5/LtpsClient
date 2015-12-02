package ltps1516.gr121gr122.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rob on 01-12-15.
 */

public class Product {
    @JsonProperty private String name;
    @JsonProperty private double price;
    @JsonProperty private String description;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
