package ltps1516.gr121gr122.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.TextAlignment;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 07-01-16.
 */
public class CustomLabel<T extends Model> extends Label {
    private T model;
    private ObjectMapper mapper;

    public CustomLabel(T model, String text) {
        this.setOnDragDetected(this::onDragDetected);
        this.setTextAlignment(TextAlignment.CENTER);

        this.model = model;
        this.mapper = new ObjectMapper();

        this.textProperty().set(text);
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
