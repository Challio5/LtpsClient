package ltps1516.gr121gr122.model.machine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 07-12-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MachineStatus implements Model {
    private IntegerProperty statusId;
    private StringProperty description;

    public MachineStatus(
            @JsonProperty("statusId") int statusId,
            @JsonProperty("description") String description) {
        this.statusId = new SimpleIntegerProperty(statusId);
        this.description = new SimpleStringProperty(description);
    }

    // Testconstructor
    public MachineStatus() {
        this.description = new SimpleStringProperty("Hard gaan");
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String toString() {
        return "MachineStatus{" +
                "description=" + description +
                '}';
    }

    @Override
    @JsonProperty("statusId")
    public long getId() {
        return statusId.get();
    }
}
