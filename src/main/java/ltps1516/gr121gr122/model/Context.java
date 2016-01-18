package ltps1516.gr121gr122.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import ltps1516.gr121gr122.model.machine.Machine;
import ltps1516.gr121gr122.model.machine.Stock;
import ltps1516.gr121gr122.model.user.Order;
import ltps1516.gr121gr122.model.user.Product;
import ltps1516.gr121gr122.model.user.ProductOrder;
import ltps1516.gr121gr122.model.user.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by rob on 02-12-15.
 */
public class Context {
    // Context
    private static Context ourInstance = new Context();

    private Scene currentScene;

    // Model
    private ObjectProperty<Machine> machine;
    private ObjectProperty<User> user;
    private DoubleProperty balance;

    private ObservableList<Product> productList;
    private ObservableList<ProductOrder> orderList;

    private ReadOnlyProperty<Order> selectedOrder;
    private ReadOnlyProperty<ProductOrder> selectedProductOrder;
    private ReadOnlyProperty<Stock> selectedStock;

    private LongProperty nfcId;

    // Properties
    private Properties properties;

    public static Context getInstance() {
        return ourInstance;
    }

    private Context() {
        File file = new File("config.properties");
        this.setPropertyPath(file);

        this.machine = new SimpleObjectProperty<>();
        this.user = new SimpleObjectProperty<>(new User());
        this.balance = new SimpleDoubleProperty();

        this.productList = FXCollections.observableArrayList();
        this.orderList = FXCollections.observableArrayList();

        this.nfcId = new SimpleLongProperty();
    }

    public void reinitialize() {
        ourInstance = new Context();
    }

    /* Getter // Setter */
    // Machine
    public Machine getMachine() {
        return machine.get();
    }

    public ObjectProperty<Machine> machineProperty() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine.set(machine);
    }

    // User
    public User getUser() {
        return user.get();
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    public void setUser(User user) {
        this.user.set(user);
    }

    // Balance
    public double getBalance() {
        return balance.get();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    // NfcId
    public long getNfcId() {
        return nfcId.get();
    }

    public LongProperty nfcIdProperty() {
        return nfcId;
    }

    public void setNfcId(long nfcId) {
        this.nfcId.set(nfcId);
    }

    // ProductList
    public ObservableList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ObservableList<Product> productList) {
        this.productList = productList;
    }

    // OrderList
    public ObservableList<ProductOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(ObservableList<ProductOrder> orderList) {
        this.orderList = orderList;
    }

    // Properties
    public Properties getProperties() {
        return properties;
    }

    public void setPropertyPath(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            properties = new Properties();
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Order
    public Order getSelectedOrder() {
        return selectedOrder.getValue();
    }

    public ReadOnlyProperty<Order> selectedOrderProperty() {
        return selectedOrder;
    }

    public void setSelectedOrderProperty(ReadOnlyProperty<Order> selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    // Productorder
    public ProductOrder getSelectedProductOrder() {
        return selectedProductOrder.getValue();
    }

    public ReadOnlyProperty<ProductOrder> selectedProductOrderProperty() {
        return selectedProductOrder;
    }

    public void setSelectedProductOrderProperty(ReadOnlyProperty<ProductOrder> selectedProductOrder) {
        this.selectedProductOrder = selectedProductOrder;
    }

    // Stock
    public Stock getSelectedStock() {
        return selectedStock.getValue();
    }

    public ReadOnlyProperty<Stock> selectedStockProperty() {
        return selectedStock;
    }

    public void setSelectedStockProperty(ReadOnlyProperty<Stock> selectedStock) {
        this.selectedStock = selectedStock;
    }
}
