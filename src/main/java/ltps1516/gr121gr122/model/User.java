package ltps1516.gr121gr122.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rob on 01-12-15.
 */

public class User {
    @JsonProperty private int userId;
    @JsonProperty private String username;
    @JsonProperty private String password;
    @JsonProperty private String name;
    @JsonProperty private String email;
    @JsonProperty private String telephone;
    @JsonProperty private boolean enabled;
    @JsonProperty private double balance;
    @JsonProperty private List<Order> orders;

    public User() {
        orders = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", enabled=" + enabled +
                ", balance=" + balance +
                ", orders=" + orders +
                '}';
    }
}
