package ltps1516.gr121gr122.model.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

/**
 * Created by rob on 17-01-16.
 */
public class OrderStatus {
    private static ObservableList<Color> colors;

    public static ObservableList<Color> getColors() {
        if(colors == null) {
            colors = FXCollections.observableArrayList(
                    Color.color(0.0,1.0,0.0,0.3),  // Green
                    Color.color(1.0,0.6,0.0,0.3),  // Orange
                    Color.color(1.0,0.0,0.0,0.3)); // Red
        }

        return colors;
    }
}
