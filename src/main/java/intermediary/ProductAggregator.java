package intermediary;

import models.SupplyReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductAggregator {

    public ProductAggregator() {

    }

    public HashMap<String, SupplyReply> selectBestProducts(List<SupplyReply> supplyReplies) {
        HashMap<String, SupplyReply> bestSupplies = new HashMap<>();

        for (int i = 0; i < supplyReplies.size(); i++) {

            // if productCode already present
            if(bestSupplies.containsKey(supplyReplies.get(i).getProductCode())) {
                SupplyReply currentBestSupply = bestSupplies.get(supplyReplies.get(i).getProductCode());

                if(currentBestSupply.getDeliveryTime() > supplyReplies.get(i).getDeliveryTime()) {
                    bestSupplies.put(supplyReplies.get(i).getProductCode(), supplyReplies.get(i));
                }
            } else {
                bestSupplies.put(supplyReplies.get(i).getProductCode(), supplyReplies.get(i));
            }

        }

        return bestSupplies;
    }
}
