package ltps1516.gr121gr122.control;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ltps1516.gr121gr122.control.comport.ComPort;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.machine.Machine;
import ltps1516.gr121gr122.model.user.Product;
import ltps1516.gr121gr122.model.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by rob on 30-11-15.
 */

public class Apl extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create logger
        Logger logger = LogManager.getLogger(Apl.class);
        logger.info("Application started");

        // Load FXML view
        Parent view = new FXMLLoader().load(this.getClass().getResourceAsStream("/view/inlog/view.fxml"));

        // Show view in stage
        Stage stage = new Stage();
        stage.setScene(new Scene(view));
        stage.setTitle("VendingMachine");
        stage.show();
    }
}
