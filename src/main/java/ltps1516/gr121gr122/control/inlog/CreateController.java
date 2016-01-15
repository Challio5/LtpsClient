package ltps1516.gr121gr122.control.inlog;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.Model;
import ltps1516.gr121gr122.model.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.View;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rob on 05-01-16.
 */
public class CreateController {

    // Model
    private Context context;

    // View
    @FXML private GridPane gridPane;

    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField name;
    @FXML private TextField email;
    @FXML private TextField telephone;

    @FXML private Button submitButton;

    // Controller
    private ApiController controller;

    // Logger
    private Logger logger;
    private BooleanProperty valid;

    public void initialize() {
        // Model
        context = Context.getInstance();

        // Control
        controller = new ApiController();

        // Logger
        logger = LogManager.getLogger(this.getClass().getName());

        // Validation stylinh
        String valid = "-fx-border-color: none";
        String invalid = "-fx-border-color: red";

        // Validate input
        gridPane.getChildren().stream().filter(node -> node instanceof TextField).forEach(node -> {
            TextField textField = (TextField) node;

            if(textField.getId().equals("telephone")) {
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(!newValue.matches("\\d{5,}")) textField.setStyle(invalid);
                    else textField.setStyle(valid);
                });
            } else if (textField.getId().equals("email")) {
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) textField.setStyle(invalid);
                    else textField.setStyle(valid);
                });
            } else {
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(!newValue.matches("\\w{3,}")) textField.setStyle(invalid);
                    else textField.setStyle(valid);
                });
            }
        });

        if(true) { //controller.serverCheck()
            // Bind validation result
            submitButton.disableProperty().bind(
                    username.styleProperty().isNotEqualTo(valid).or(
                    password.styleProperty().isNotEqualTo(valid)).or(
                    name.styleProperty().isNotEqualTo(valid)).or(
                    email.styleProperty().isNotEqualTo(valid)).or(
                    telephone.styleProperty().isNotEqualTo(valid)));
        } else {
            submitButton.setDisable(true);
        }
    }

    @FXML
    private void submit(ActionEvent event) {
        User user = new User();

        gridPane.getChildren().stream().filter(node -> node instanceof TextField).forEach(node -> {
            TextField field = (TextField) node;

            Method[] methods = User.class.getMethods();
            for (Method method : methods) {
                String name = "set" + field.getId().substring(0, 1).toUpperCase()
                        + field.getId().substring(1);

                if (method.getName().equals(name)) {
                    try {
                        method.invoke(user, field.getText());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Model model = controller.getCrudController().create(user);
        logger.info("Entity added to context: " + model);
        if(model instanceof User) {
            context.setUser((User) model);
            ViewController.createMainView();
        }
    }
}
