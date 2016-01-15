package ltps1516.gr121gr122.control.main;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import ltps1516.gr121gr122.control.converter.ProductConverter;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.machine.Stock;
import ltps1516.gr121gr122.model.user.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by rob on 05-01-16.
 */
public class StockController {
    // Context
    private Context context;
    private Logger logger;

    // Stock table
    @FXML private TableView<Stock> stockTable;

    @FXML private TableColumn<Stock, Product> stockProductColumn;
    @FXML private TableColumn<Stock, Integer> stockAmountColumn;

    public void initialize() {
        logger = LogManager.getLogger(this.getClass().getName());

        context = Context.getInstance();
        context.setSelectedStockProperty(stockTable.getSelectionModel().selectedItemProperty());

        // Set tablecells for stock
        stockProductColumn.setCellFactory(TextFieldTableCell.forTableColumn(new ProductConverter()));
        stockAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Set properties to columns
        stockProductColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        stockAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // View stock
        stockTable.setItems(context.getMachine().getStockList());
    }// Register NFC card
}
