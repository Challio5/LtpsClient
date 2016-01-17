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
        this.setPadding(new Insets(10));
        this.setPrefWidth(180);
        this.setStyle("-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 1px");

        this.setOnDragDetected(this::onDragDetected);
        this.setTextAlignment(TextAlignment.CENTER);
        this.setAlignment(Pos.CENTER);

        this.model = model;
        this.mapper = new ObjectMapper();

        if(model instanceof Product) {
            Product product = (Product) model;
            String format = String.format("%s%n%s%n€%.2f" , product.getName(), product.getDescription(), product.getPrice());
            this.textProperty().set(format);
        }
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

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
