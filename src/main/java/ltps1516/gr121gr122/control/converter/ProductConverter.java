package ltps1516.gr121gr122.control.converter;

import javafx.util.StringConverter;
import ltps1516.gr121gr122.model.user.Product;

import java.util.HashMap;

/**
 * Created by rob on 02-12-15.
 */
public class ProductConverter extends StringConverter<Product> {
    private HashMap<String, Product> productHashMap = new HashMap<>();

    @Override
    public String toString(Product product) {
        productHashMap.put(product.getName(), product);
        return product.getName();
    }

    @Override
    public Product fromString(String string) {
        return productHashMap.get(string);
    }
}
