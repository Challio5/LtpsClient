package ltps1516.gr121gr122.control.main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.control.comport.ComPort;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.Model;
import ltps1516.gr121gr122.model.user.NfcCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by rob on 11-01-16.
 * Controller which controls the NFC view where cards can be coupled to an account
 */

public class NfcController {

    // Logger
    private Logger logger;

    // Control
    private ApiController controller;

    // View
    @FXML private ListView<NfcCard> nfcView;
    @FXML private Button nfcButton;

    /**
     * Method that initialises the controller when the view is created
     * Binds the appropriate content to the view
     */
    public void initialize() {
        this.logger = LogManager.getLogger(this.getClass().getName());
        this.controller = new ApiController();

        nfcView.setCellFactory(this::cellFactory);
        nfcView.setItems(Context.getInstance().getUser().getNfcList());

        nfcButton.disableProperty().bind(ComPort.getInstance().nfcSerialPortProperty().isNull());
    }

    /**
     * Method reference for creating cells
     * @param param Listview for which the cells are created
     * @return Listcells for the listview which is passes as parameter
     */
    private ListCell<NfcCard> cellFactory(ListView<NfcCard> param) {
        ListCell<NfcCard> card = new ListCell<NfcCard>() {
            @Override
            protected void updateItem(NfcCard item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null && !empty) {
                    this.textProperty().bind(item.cardIdProperty().asString());
                }
            }
        };
        card.setStyle("-fx-alignment: center");
        return card;
    }

    /**
     * Eventmethod which fires when the add NFC button is clicked
     * Let's the comport read the nfc id of the card and listens to a change of context
     * Gives feedback with alert windows that close after three seconds
     */
    @FXML
    public void connectNFC() {
        // Create dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setStyle("-fx-font: 20px Modena; -fx-min-width: 600px;");
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);

        // Bind content text
        StringProperty content = new SimpleStringProperty("Please hold your card for the NFC reader");
        alert.contentTextProperty().bind(content);

        // Disable button and show
        alert.show();

        // Toggle interrupt for reading NFC
        ComPort.getInstance().turnOnNfcInterrupt(10, TimeUnit.SECONDS);

        // Listen to new NFC card
        Context.getInstance().nfcIdProperty().addListener((observable, oldValue, newValue) -> {
            // Create NFC object
            NfcCard nfc = new NfcCard(newValue.longValue(), Context.getInstance().getUser().getId());
            logger.info("New nfc card created: " + nfc);

            // Add to API and context
            Model model = controller.getCrudController().create(nfc);
            logger.info("NfcCard returned from server: " + model);

            // Check returned model from API
            if(model != null) {
                logger.info("Nfc added to API and context");
                Platform.runLater(() -> content.set("NFC coupled to your account"));
                Context.getInstance().getUser().getNfcList().add(nfc);
            } else {
                Platform.runLater(() -> content.set("NFC already coupled to an account"));
                logger.warn("Null returned from server");
            }

            // Shows alert when not already shown
            if(!alert.isShowing()) {
                Platform.runLater(alert::show);
            }

            // Close after 3 seconds
            Timeline idlestage = new Timeline(new KeyFrame(
                Duration.seconds(3), event1 ->
                    Platform.runLater(() -> {
                        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).fire();
                        logger.warn("Close window");
                    })
            ));

            // Play timeline once
            idlestage.setCycleCount(1);
            idlestage.play();
        });
    }
}
