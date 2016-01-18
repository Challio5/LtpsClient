package ltps1516.gr121gr122.view;

import com.sun.javafx.charts.Legend;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import ltps1516.gr121gr122.model.user.OrderStatus;

/**
 * Created by rob on 17-01-16.
 */
public class CustomLegend extends Legend {
    public CustomLegend() {
        this.setMaxHeight(Button.USE_PREF_SIZE);
        this.setMaxWidth(400);
        this.setAlignment(Pos.CENTER);

        LegendItem item1 = new LegendItem("Ordered", new Rectangle(10, 10, OrderStatus.ORDERED.color()));
        LegendItem item2 = new LegendItem("OutOfStock", new Rectangle(10, 10, OrderStatus.OUTOFSTOCK.color()));
        LegendItem item3 = new LegendItem("OutOfBalance", new Rectangle(10, 10, OrderStatus.OUTOFBALANCE.color()));
        LegendItem item4 = new LegendItem("Collected", new Rectangle(10, 10, OrderStatus.PAYDANDCOLLECTED.color()));

        this.getItems().addAll(item1, item2, item3, item4);
    }
}
