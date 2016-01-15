package ltps1516.gr121gr122.control.main;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.view.CustomNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by rob on 02-12-15.
 */
public class ViewController {
    // Model
    private Context context;

    // Root and center nodes
    @FXML BorderPane borderPane;

    // Top/Bottom
    @FXML CustomNavigation navigation;
    @FXML ToolBar machineStatus;
    @FXML GridPane grid;

    // Machine status
    @FXML private Label statusLabel;

    // Logger
    private Logger logger;

    /**
     *
     */
    public void initialize() {
        // Get model instance
        context = Context.getInstance();

        try {
            Pane pane1 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/main/product.fxml"));
            Pane pane2 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/main/order.fxml"));
            Pane pane3 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/main/account.fxml"));
            Pane pane4 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/main/nfc.fxml"));
            Pane pane5 = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/main/stock.fxml"));

            // Set id for navigation
            pane1.setId("Products");
            pane2.setId("Orders");
            pane3.setId("Account");
            pane4.setId("NFC");
            pane5.setId("Stock");

            navigation.setBorderPane(borderPane);
            navigation.getItems().addAll(pane1, pane2, pane3, pane4, pane5);
            navigation.getSelectionModel().selectFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Machine status
        statusLabel.setText("User: " + context.getUser().getName() +
                " on machine at " + context.getMachine().getLocation() +
                " with status: " + context.getMachine().getMachineStatus().getDescription());

        // Logger
        logger = LogManager.getLogger(this.getClass().getName());
    }

    /**
     * Event handler to logout and load the preloader
     * @param event Event which is fired when clicking the logout button
     */
    @FXML
    public void logout(ActionEvent event) {
        // Reset context
        Context.getInstance().reinitialize();

        // Create view
        try {
            Parent view = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/inlog/view.fxml"));

            Scene scene = new Scene(view);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
