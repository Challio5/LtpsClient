package ltps1516.gr121gr122.control.main;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.control.api.CrudController;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rob on 05-01-16.
 */
public class AccountController {
    private Context context;
    private Logger logger;

    private CrudController crudController;

    @FXML private GridPane gridPane;

    @FXML private void initialize() {
        context = Context.getInstance();
        logger = LogManager.getLogger(this.getClass().getName());

        crudController = new ApiController().getCrudController();

        gridPane.getChildren().stream().filter(node -> node instanceof TextField).forEach(node -> {
            TextField field = (TextField) node;
            field.focusedProperty().addListener(this::focusListener);

            User user = context.getUser();

            Method[] methods = User.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(field.getId() + "Property")) {
                    try {
                        Object object = method.invoke(user);
                        if (object instanceof StringProperty) {
                            StringProperty property = (StringProperty) object;
                            field.textProperty().bindBidirectional(property);
                        } else if (object instanceof DoubleProperty) {
                            DoubleProperty property = (DoubleProperty) object;
                            field.textProperty().bindBidirectional(property, new NumberStringConverter());
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void focusListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if(newValue) {
            TextField textField = (TextField) gridPane.getScene().getFocusOwner();
            textField.setEditable(true);
            textField.setStyle("-fx-opacity: 1.0;");
        } else {
            gridPane.getChildren().stream().filter(i -> i instanceof TextField).forEach(j -> {
                ((TextField) (j)).setEditable(false);
                j.setStyle("-fx-opacity: 0.4;");
            });

            // Post user
            crudController.update(Context.getInstance().getUser());
        }
    }
}
