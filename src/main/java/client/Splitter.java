package client;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import models.Order;
import models.Serializer;
import models.SupplyRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Splitter {
    private final static String EXCHANGE_NAME = "SupplyRequestTopic";
    private ConnectionFactory factory = new ConnectionFactory();
    private Serializer serializer;

    public Splitter() {
        factory.setHost("localhost");
        serializer = new Serializer();
    }


    public void sendOrder(Order order) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            List<String> productCodes = order.getProductCodes();

            for (int i = 0; i < order.getProductCodes().size(); i++) {
                SupplyRequest req = new SupplyRequest(productCodes.get(i), order.getOrderId());
                channel.basicPublish(EXCHANGE_NAME, "", null, serializer.classToString(req).getBytes());
                System.out.println(" [x] Sent supplyRequest for product: '" + productCodes.get(i) + "'");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
