package ltps1516.gr121gr122.view;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Created by rob on 14-01-16.
 */
public class CustomNavigation extends ListView<Pane> {

    private BorderPane borderPane;
    private VBox vBox;

    public CustomNavigation() {
        this.setCellFactory(this::cellFactory);
        this.getSelectionModel().selectedIndexProperty().addListener(this::indexListener);
    }

    public void setVBox(VBox vBox) {
        this.vBox = vBox;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    private void indexListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        Pane pane = this.getItems().get(newValue.intValue());

        TranslateTransition transition = new TranslateTransition(Duration.millis(300), pane);

        if(newValue.intValue() > oldValue.intValue()) {
            transition.setFromX(800);
            transition.setToX(0);
        } else {
            transition.setFromX(-800);
            transition.setToX(0);
        }

        transition.play();

        if(borderPane != null) {
            borderPane.setCenter(pane);
        }
        else if(vBox != null) {
            vBox.getChildren().set(0, pane);
        }
    }

    private ListCell<Pane> cellFactory(ListView<Pane> param) {
        return new ListCell<Pane>() {
            @Override
            protected void updateItem(Pane item, boolean empty) {
                super.updateItem(item, empty);

                this.setPrefWidth(124.0);
                this.setAlignment(Pos.CENTER);

                if(item != null && !empty) {
                    this.setText(item.getId());
                } else {
                    this.setText("");
                }
            }
        };
    }
}
