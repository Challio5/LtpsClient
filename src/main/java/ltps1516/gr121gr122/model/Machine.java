package ltps1516.gr121gr122.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rob on 01-12-15.
 */

public class Machine {
    @JsonProperty private String location;
    @JsonProperty private String description;

    @Override
    public String toString() {
        return "Machine{" +
                "location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
