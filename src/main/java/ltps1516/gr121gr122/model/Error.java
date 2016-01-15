package ltps1516.gr121gr122.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by rob on 04-12-15.
 */
public class Error {
    private Date timestamp;
    private int status;
    private String error;
    private String exception;
    private String message;
    private String path;

    public Error(
            @JsonProperty("timestamp") Date timestamp,
            @JsonProperty("status") int status,
            @JsonProperty("error") String error,
            @JsonProperty("exception") String exception,
            @JsonProperty("message") String message,
            @JsonProperty("path") String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.path = path;
    }

    @JsonIgnore
    public Error() {}

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", exception='" + exception + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
