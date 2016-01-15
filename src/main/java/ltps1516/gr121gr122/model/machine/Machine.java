package ltps1516.gr121gr122.model.machine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 01-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Machine implements Model {
    private IntegerProperty machineId;
    private StringProperty location;
    private StringProperty description;
    private IntegerProperty statusId;
    private ObjectProperty<Status> machineStatus;

    @JsonIgnore
    private ObservableList<Stock> stockList;

    public Machine(
            @JsonProperty("machineId") int machineId,
            @JsonProperty("location") String location,
            @JsonProperty("description") String description,
            @JsonProperty("statusId") int statusId,
            @JsonProperty("status") Status machineStatus,
            @JsonProperty("stock") Stock[] stockList)
    {
        this.machineId = new SimpleIntegerProperty(machineId);
        this.location = new SimpleStringProperty(location);
        this.description = new SimpleStringProperty(description);
        this.statusId = new SimpleIntegerProperty(statusId);
        this.machineStatus = new SimpleObjectProperty<>(machineStatus);

        this.stockList = FXCollections.observableArrayList(stock ->
            new Observable[] {stock.amountProperty(), stock.locationXProperty(), stock.locationYProperty()}
        );

        this.stockList.addAll(stockList);
    }

    // Test constructor
    public Machine() {
        this.machineId = new SimpleIntegerProperty(1);
        this.location = new SimpleStringProperty("Casanova");
        this.description = new SimpleStringProperty("SuperMachine");
        this.statusId = new SimpleIntegerProperty(3);
        this.machineStatus = new SimpleObjectProperty<>(new Status());
        this.stockList = FXCollections.observableArrayList(new Stock());
    }

    @Override
    @JsonProperty("machineId")
    public long getId() {
        return machineId.get();
    }

    public IntegerProperty machineIdProperty() {
        return machineId;
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public int getStatusId() {
        return statusId.get();
    }

    public IntegerProperty statusIdProperty() {
        return statusId;
    }

    public Status getMachineStatus() {
        return machineStatus.get();
    }

    public ObjectProperty<Status> machineStatusProperty() {
        return machineStatus;
    }

    public ObservableList<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(ObservableList<Stock> stockList) {
        this.stockList = stockList;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId=" + machineId +
                ", location=" + location +
                ", description=" + description +
                ", statusId=" + statusId +
                ", machineStatus=" + machineStatus +
                ", stockList=" + stockList +
                '}';
    }
}
