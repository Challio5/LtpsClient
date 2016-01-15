package ltps1516.gr121gr122.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 01-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Model {
    private IntegerProperty userId;
    private StringProperty username;
    private StringProperty password;
    private StringProperty name;
    private StringProperty email;
    private StringProperty telephone;
    private BooleanProperty enabled;
    private DoubleProperty balance;

    @JsonIgnore
    private ObservableList<Order> orders;

    @JsonIgnore
    private ObservableList<NfcCard> nfcList;

    public User(
            @JsonProperty("userId") int userId,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("telephone") String telephone,
            @JsonProperty("enabled") boolean enabled,
            @JsonProperty("balance") double balance,
            @JsonProperty("orders") Order[] orders,
            @JsonProperty("nfcCards") NfcCard[] nfcs) {
        this.userId = new SimpleIntegerProperty(userId);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.telephone = new SimpleStringProperty(telephone);
        this.enabled = new SimpleBooleanProperty(enabled);
        this.balance = new SimpleDoubleProperty(balance);

        this.orders = FXCollections.observableArrayList(orders);
        this.nfcList = FXCollections.observableArrayList(nfcs);
    }

    public User() {
        this.userId = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.telephone = new SimpleStringProperty();
        this.enabled = new SimpleBooleanProperty();
        this.balance = new SimpleDoubleProperty();

        this.orders = FXCollections.observableArrayList();
        this.nfcList = FXCollections.observableArrayList();
    }

    // Test constructor
    public User(String test) {
        this.userId = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty("ratata");
        this.password = new SimpleStringProperty("123456");
        this.name = new SimpleStringProperty("Test");
        this.email = new SimpleStringProperty("Bobasd");
        this.telephone = new SimpleStringProperty("123456789");
        this.enabled = new SimpleBooleanProperty();
        this.balance = new SimpleDoubleProperty();

        this.orders = FXCollections.observableArrayList(new Order(""));
        this.nfcList = FXCollections.observableArrayList(new NfcCard());
    }

    public boolean addOrder(Order order) {
        return orders.add(order);
    }

    @Override
    @JsonProperty("userId")
    public long getId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getTelephone() {
        return telephone.get();
    }

    public StringProperty telephoneProperty() {
        return telephone;
    }

    public boolean getEnabled() {
        return enabled.get();
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public double getBalance() {
        return balance.get();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    @JsonIgnore
    public ObservableList<Order> getOrders() {
        return orders;
    }

    @JsonIgnore
    public ObservableList<NfcCard> getNfcList() {
        return nfcList;
    }

    // Setter
    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setTelephone(String telephone) {
        this.telephone.set(telephone);
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public void setOrders(ObservableList<Order> orders) {
        this.orders = orders;
    }

    public void setNfcList(ObservableList<NfcCard> nfcList) {
        this.nfcList = nfcList;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username +
                ", password=" + password +
                ", name=" + name +
                ", email=" + email +
                ", telephone=" + telephone +
                ", enabled=" + enabled +
                ", balance=" + balance +
                ", orders=" + orders +
                ", nfcList=" + nfcList +
                '}';
    }
}
