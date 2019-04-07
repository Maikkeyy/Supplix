package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private List<String> productCodes = new ArrayList<>();

    public Order(List<String> productCodes) {
        orderId = UUID.randomUUID().toString();
        this.productCodes = productCodes;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<String> getProductCodes() {
        return productCodes;
    }
}
