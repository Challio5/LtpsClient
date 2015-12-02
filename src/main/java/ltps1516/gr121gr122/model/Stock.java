package ltps1516.gr121gr122.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rob on 01-12-15.
 */

public class Stock {

    @JsonProperty private Machine machine;
    @JsonProperty private Product product;
    @JsonProperty private int amount;
    @JsonProperty private int x;
    @JsonProperty private int y;

    @Override
    public String toString() {
        return "Stock{" +
                "machine=" + machine +
                ", product=" + product +
                ", amount=" + amount +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
