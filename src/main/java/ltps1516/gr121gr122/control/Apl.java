package ltps1516.gr121gr122.control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ltps1516.gr121gr122.control.api.ApiController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by rob on 30-11-15.
 */

// ToDo PATCH USER enable account
// ToDo STOCK if product not available at all
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
