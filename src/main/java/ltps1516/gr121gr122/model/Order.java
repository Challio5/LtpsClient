package ltps1516.gr121gr122.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rob on 01-12-15.
 */

public class Order {
    @JsonProperty private int orderId;

    @JsonProperty private int statusId;
    @JsonProperty private String code;

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", statusId=" + statusId +
                ", code='" + code + '\'' +
                '}';
    }
}
