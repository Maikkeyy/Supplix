package models;

import java.io.Serializable;

public class SupplyReply implements Serializable {
    private String productCode;
    private String orderId;
    private Integer deliveryTime;
    private String supplierName;
//    private String clientId; // for using multiple clients

    public SupplyReply() {

    }

    public SupplyReply(String productCode, String orderId, Integer deliveryTime, String supplierName) {
        this.productCode = productCode;
        this.orderId = orderId;
        this.deliveryTime = deliveryTime;
        this.supplierName = supplierName;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
