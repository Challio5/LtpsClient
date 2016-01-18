package ltps1516.gr121gr122.control.inlog;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.control.comport.ComPort;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


/**
 * Created by rob on 02-12-15.
 */
public class InlogController {
    // View
    @FXML private VBox root;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button submitButton;

    private Label errorLabel;

    // Controller
    private ApiController apiController;

    // Logger
    private final static Logger logger = LogManager.getLogger(InlogController.class);

    /**
     * Method to initialize the controller related to the inlogview
     * Adds
     */
    @FXML private void initialize() {
        // Create apiController
        apiController = new ApiController();

        // Create label
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        // Read machineId from properties
        int machineId = Integer.parseInt(
                Context.getInstance().getProperties().getProperty("machineId"));

        try {
            // Get machinedata with id
            apiController.getMachineData(machineId);
            logger.info("Successfully got data for machine with id: " + machineId);
        } catch (WebApplicationException e) {
            // Log exception
            logger.warn(e.getMessage());

            // Disable login fields
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            submitButton.setDisable(true);

            // Create error label
            root.getChildren().add(errorLabel);

            // Read status code and give feedback to user
            int statusCode = e.getResponse().getStatus();
            switch (Response.Status.fromStatusCode(statusCode)) {
                case UNAUTHORIZED:
                    errorLabel.setText("Machine has no access to data, contact admin");
                    break;
                case NO_CONTENT:
                    errorLabel.setText("Machine does not exist");
                    break;
            }
        }

        // Listen for NFC inlog card
        Context.getInstance().userProperty().addListener(this::nfcInlogListener);

        // Turn on listener
        ComPort.getInstance().toggleNfcInlogInterrupt();
    }

    /**
     * Method that listens to a NFC added to the context
     * Displays a dialog containing information if succeeded or failed
     *
     * @param observable The value being observed
     * @param oldValue The old value before the change
     * @param newValue The new value after the change
     */
    private void nfcInlogListener(Observable observable, User oldValue, User newValue) {
        // User had logged in with NFC
        if(newValue != null) {
            Platform.runLater(ViewController::createMainView);

            ComPort.getInstance().toggleNfcInlogInterrupt();

            logger.info("Logged in with Nfc, main view is created");
            Context.getInstance().userProperty().removeListener(this::nfcInlogListener);
        // User is not recognize bij NFC
        } else {
            Platform.runLater(() -> {
                // Show alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.getDialogPane().setStyle("-fx-font: 20px Modena; -fx-min-width: 600px");
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("NFC not recognized as account");
                alert.show();

                // Close after 3 seconds
                Timeline idlestage = new Timeline(new KeyFrame(
                        Duration.seconds(3), event1 -> {
                    ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).fire();
                    logger.warn("Close window");
                }
                ));

                idlestage.setCycleCount( 1 );
                idlestage.play();
            });
        }
    }

    /**
     *
     * @param event
     * @throws Exception
     */
    @FXML private void submit(Event event) throws Exception {
        // Return when keyevent not equals enter
        if(event instanceof KeyEvent) {
            if (((KeyEvent) event).getCode() != KeyCode.ENTER) return;
        }

        // Create APIcontroller
        try {
            apiController.login(usernameField.getText(), passwordField.getText());
            logger.info("Successfully logged in with username: " + usernameField.getText());

            // Creat view
            ViewController.createMainView();
        }
        catch (WebApplicationException e) {
            logger.warn(e.getMessage());

            usernameField.setStyle("-fx-background-color: red");
            passwordField.setStyle("-fx-background-color: red");

            // Read status code and give feedback to user
            int statusCode = e.getResponse().getStatus();
            switch (Response.Status.fromStatusCode(statusCode)) {
                case UNAUTHORIZED:
                    errorLabel.setText("Machine has no access to data, contact admin");
                    break;
            }
        }
    }
}
