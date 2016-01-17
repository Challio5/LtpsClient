package ltps1516.gr121gr122.view;

import javafx.scene.control.Button;
import javafx.scene.control.Pagination;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rob on 17-01-16.
 */
public class CustomPagination extends Pagination {
    public CustomPagination() {
        super();

        List<Button> buttonList = this.getChildren().stream()
                .filter(item -> item instanceof Button).map(Button.class::cast).collect(Collectors.toList());

        System.out.println(buttonList.size());
    }
}
