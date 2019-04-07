package models;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedOrder {
    private String orderId;
    private List<SupplyReply> supplyReplies = new ArrayList<>();

    public ConfirmedOrder() {

    }

    public ConfirmedOrder(String orderId, List<SupplyReply> supplyReplies) {
        this.orderId = orderId;
        this.supplyReplies = supplyReplies;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<SupplyReply> getSupplyReplies() {
        return supplyReplies;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setSupplyReplies(List<SupplyReply> supplyReplies) {
        this.supplyReplies = supplyReplies;
    }
}
