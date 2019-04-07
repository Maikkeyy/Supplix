package intermediary;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import models.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Intermediary {
    private final static String QUEUE_NAME = "SupplyReplyQueue";
    private static List<SupplyReply> supplyReplyList = new ArrayList<>();
    private static final long INTERVAL = 800;
    private static Timer timer = new Timer();
    private static boolean firstReplyReceived = false;
    private static ProductAggregator productAggregator = new ProductAggregator();
    private static OrderAggregator orderAggregator = new OrderAggregator();
    private static Serializer serializer;

    private final static String EXCHANGE_NAME = "ConfirmedOrderQueue";
    private static ConnectionFactory factory = new ConnectionFactory();

    public static void main(String[] argv) throws Exception {
        serializer = new Serializer();
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");

            SupplyReply supplyReply = (SupplyReply) serializer.classFromString(message, SupplyReply.class);
            supplyReplyList.add(supplyReply);

            if (firstReplyReceived == false) {
                firstReplyReceived = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Sending best products to productAggregator");
                        HashMap<String, SupplyReply> bestSupplies = productAggregator.selectBestProducts(supplyReplyList);

//                        for (Map.Entry<String, SupplyReply> entry : bestSupplies.entrySet()) {
//                            String key = entry.getKey();
//                            Object value = entry.getValue();
//                            System.out.println(key);
//                            System.out.println(serializer.classToString(value));
//                        }

                        HashMap<String, List<SupplyReply>> productsByOrder = orderAggregator.groupProductsPerOrder(bestSupplies);

                        for (Map.Entry<String, List<SupplyReply>> entry : productsByOrder.entrySet()) {
                            String key = entry.getKey();
                            List<SupplyReply> bestSuppliesPerOrder = entry.getValue();

                            ConfirmedOrder order = new ConfirmedOrder(key, bestSuppliesPerOrder);


                            for (int i = 0; i < supplyReplyList.size(); i++) {
                                System.out.println(serializer.classToString(supplyReplyList.get(i)));
                            }

                            sendConfirmedOrder(order);
                        }

                    }
                }, INTERVAL);
            }

        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });

    }

    private static void sendConfirmedOrder(ConfirmedOrder order) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(EXCHANGE_NAME, false, false, false, null);

            channel.basicPublish("", EXCHANGE_NAME, null, serializer.classToString(order).getBytes());
            System.out.println(" [x] Sent confirmedOrder for orderId: '" + order.getOrderId() + "'");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
