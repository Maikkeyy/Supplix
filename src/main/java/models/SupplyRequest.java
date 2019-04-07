package models;

import java.io.Serializable;

public class SupplyRequest implements Serializable {
    private String productCode;
    private String orderId;

    public SupplyRequest() {

    }

    public SupplyRequest(String productCode, String orderId) {
        this.productCode = productCode;
        this.orderId = orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
