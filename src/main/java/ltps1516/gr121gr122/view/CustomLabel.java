package ltps1516.gr121gr122.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.TextAlignment;
import ltps1516.gr121gr122.model.Model;
import ltps1516.gr121gr122.model.user.Product;

/**
 * Created by rob on 07-01-16.
 */
public class CustomLabel<T extends Model> extends Label {
    private T model;
    private ObjectMapper mapper;

    public CustomLabel(T model) {
        // Initialize fields
        this.model = model;
        this.mapper = new ObjectMapper();

        // Set padding and width of label
        this.setPadding(new Insets(10));
        this.setPrefWidth(180);

        // Align label and his text to center
        this.setTextAlignment(TextAlignment.CENTER);
        this.setAlignment(Pos.CENTER);

        // Style background as default JavaFX
        this.setStyle("-fx-background-color: -fx-box-border, -fx-control-inner-background; -fx-background-insets: 0, 1;");

        // Enable dragging of label
        this.setOnDragDetected(this::onDragDetected);

        // Set text if productLabel
        if(model instanceof Product) {
            Product product = (Product) model;
            String format = String.format("%s%n%s%n€%.2f" , product.getName(), product.getDescription(), product.getPrice());
            this.textProperty().set(format);
        }
    }

    // Get model attached to label
    public T getModel() {
        return model;
    }

    // Set model attached to label
    public void setModel(T model) {
        this.model = model;
    }

    // Deserialize label as JSON
    public String toJson() {
        String result = "";

        try {
            result = mapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Serialize productorder when dragged
    private void onDragDetected(MouseEvent event) {
        if(event.getSource() instanceof CustomLabel) {
            // Set transfermode
            CustomLabel label = (CustomLabel) event.getSource();
            Dragboard dragboard = label.startDragAndDrop(TransferMode.COPY);

            // Add label to clipboard
            ClipboardContent content = new ClipboardContent();
            content.putString(label.toJson());
            dragboard.setContent(content);

            event.consume();
        }
    }
}
