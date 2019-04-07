package intermediary;

import models.SupplyReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAggregator {

    public OrderAggregator() {

    }

    public HashMap<String, List<SupplyReply>> groupProductsPerOrder(HashMap<String, SupplyReply> bestSupplies) {
        HashMap<String, List<SupplyReply>> productsGroupedByOrder = new HashMap<>();

        for (Map.Entry<String, SupplyReply> entry : bestSupplies.entrySet()) {
            String key = entry.getKey(); // productCode
            SupplyReply reply = entry.getValue();

            if (productsGroupedByOrder.containsKey(reply.getOrderId())) {
                productsGroupedByOrder.get(reply.getOrderId()).add(reply);
            } else {
                List<SupplyReply> replies = new ArrayList<SupplyReply>();
                replies.add(reply);
                productsGroupedByOrder.put(reply.getOrderId(), replies);

            }

        }

        return productsGroupedByOrder;

    }

}
