package ltps1516.gr121gr122.model.user;

import javafx.scene.paint.Color;

/**
 * Created by rob on 17-01-16.
 */
public enum OrderStatus {
    ORDERED             (Color.color(0.0,1.0,0.0,0.3)),    // GREEN
    PAYD                (Color.color(1.0,0.0,0.0,0.3)),    // WHITE
    PAYDANDCOLLECTED    (Color.color(0.3,0.3,0.3,0.3)),    // Grey
    CANCELED            (Color.color(1.0,1.0,1.0,0.3)),    // WHITE
    OUTOFSTOCK          (Color.color(1.0,0.6,0.0,0.3)),    // ORANGE
    OUTOFBALANCE        (Color.color(1.0,0.0,0.0,0.3));    // RED

    private final Color color;

    private OrderStatus(Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public static OrderStatus convert(int statusId) {
        OrderStatus status = null;

        for(OrderStatus value : OrderStatus.values()) {
            if(value.ordinal() == statusId) {
                status = value;
                break;
            }
        }

        return status;
    }
}
