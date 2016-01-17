package ltps1516.gr121gr122.control.inlog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ltps1516.gr121gr122.control.Apl;
import ltps1516.gr121gr122.control.comport.ComPort;
import ltps1516.gr121gr122.view.CustomNavigation;

import java.io.IOException;

/**
 * Created by rob on 05-01-16.
 */
public class ViewController {
    // UI Controls
    @FXML private VBox root;
    @FXML private CustomNavigation navigation;

    // Static hack
    private static VBox staticRoot;

    /**
     * Method to initialize controller related to preloader
     * Loads panes with navigation to switch between logging in or creating new users
     */
    @FXML private void initialize() {
        staticRoot = root;

        try {
            // Load pane
            Pane pane1 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/inlog/inlog.fxml"));
            Pane pane2 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/inlog/create.fxml"));

            // Id for navigation
            pane1.setId("inlog");
            pane2.setId("create");

            // Initialize navigation
            navigation.setVBox(root);
            navigation.getItems().addAll(pane1, pane2);
            navigation.getSelectionModel().selectFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check comport
        ComPort comPort = ComPort.getInstance();
        if(comPort.getNfcSerialPort() == null) {
            Label label = new Label("No NFC reader connected");
            label.setStyle("-fx-text-fill: red");
            root.getChildren().add(label);
        }
        if(comPort.getVendingSerialPort() == null) {
            Label label = new Label("No motor control board connected");
            label.setStyle("-fx-text-fill: red");
            root.getChildren().add(label);
        }
    }

    /**
     * Method that creates the main view after a user has logged in
     * Gets the stage from the Apl class and sets a new scene
     */
    public static void createMainView() {
        try {
            Parent view = new FXMLLoader().load(Apl.class.getClass().getResourceAsStream("/view/main/view.fxml"));

            Stage stage = (Stage) staticRoot.getScene().getWindow();;

            Scene scene = new Scene(view);
            Platform.runLater(() -> stage.setScene(scene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
