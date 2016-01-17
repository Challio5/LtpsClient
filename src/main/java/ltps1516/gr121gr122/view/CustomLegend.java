package ltps1516.gr121gr122.view;

import com.sun.javafx.charts.Legend;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ltps1516.gr121gr122.model.user.OrderStatus;

/**
 * Created by rob on 17-01-16.
 */
public class CustomLegend extends Legend {
    public CustomLegend() {
        this.setMaxHeight(Button.USE_PREF_SIZE);
        this.setAlignment(Pos.CENTER);

        LegendItem item1 = new LegendItem("status1", new Rectangle(10, 10, OrderStatus.getColors().get(0)));
        LegendItem item2 = new LegendItem("status2", new Rectangle(10, 10, OrderStatus.getColors().get(1)));
        LegendItem item3 = new LegendItem("status3", new Rectangle(10, 10, OrderStatus.getColors().get(2)));

        this.getItems().addAll(item1, item2, item3);
    }
}
