package client;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import models.ConfirmedOrder;
import models.Order;
import models.Serializer;
import models.SupplyReply;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Client {
    private final static String QUEUE_NAME = "ConfirmedOrderQueue";
    private static Splitter splitter;
    private static Serializer serializer;
    private static ConnectionFactory factory = new ConnectionFactory();
    private static Random rand = new Random();

    public Client() throws IOException, TimeoutException {
        splitter = new Splitter();
        serializer = new Serializer();
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for confirmed orders. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            ConfirmedOrder order = (ConfirmedOrder) serializer.classFromString(message, ConfirmedOrder.class);
            List<SupplyReply> supplyReplies = order.getSupplyReplies();
            Integer totalDeliveryTime = 0;

            System.out.println("[o] Received confirmed order (" + order.getOrderId() + ")");

            for(int i = 0; i < supplyReplies.size(); i++) {
                if(supplyReplies.get(i).getDeliveryTime() > totalDeliveryTime) {
                    totalDeliveryTime = supplyReplies.get(i).getDeliveryTime();
                }

                System.out.println(serializer.classToString(supplyReplies.get(i)));
            }

            System.out.println("[o] ... with a total deliveryTime of:" + totalDeliveryTime);

        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Client client = new Client(); // needed to initialize splitter

//            String message = args.length < 1 ? "info: Hello World!" :
//                    String.join(" ", args);

            List<String> pCodes = new ArrayList<>();
//            pCodes.add("C100");
//            pCodes.add("C55");
//            pCodes.add("C22");

        for (int i = 0; i < 10; i++) { // filling inventory
            String pCode = "C" + rand.nextInt(1001);
            pCodes.add(pCode);
        }


        Order order = new Order(pCodes);
            splitter.sendOrder(order);
    }
}
