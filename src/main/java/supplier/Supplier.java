package supplier;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.inbot.testfixtures.Person;
import io.inbot.testfixtures.RandomNameGenerator;
import models.Serializer;
import models.SupplyReply;
import models.SupplyRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Supplier {
    private final static String EXCHANGE_NAME = "SupplyRequestTopic";
    private final static String SEND_EXCHANGE = "SupplyReplyQueue";
    private static HashMap<String, Integer> inventory = new HashMap<>();
    private static ConnectionFactory factory = new ConnectionFactory();
    private static Connection connection;
    private static Serializer serializer;
    private static String SUPPLIER_NAME;

    private Random rand = new Random();

    public Supplier() {
        RandomNameGenerator randomNameGenerator = new RandomNameGenerator(rand.nextInt(100));

        Person p = randomNameGenerator.nextPerson();
        SUPPLIER_NAME = p.getCompany();

        for (int i = 0; i < 50; i++) { // filling inventory
            String pCode = "C" + rand.nextInt(1001);
            Integer deliveryTime = rand.nextInt(4);
            inventory.put(pCode, deliveryTime);
        }

        // simulating for test environment
        inventory.put("C100", rand.nextInt(4));
        inventory.put("C55", rand.nextInt(4));
        inventory.put("C22", rand.nextInt(4));


        factory.setHost("localhost");
        serializer = new Serializer();
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) throws Exception {
        Supplier supplier = new Supplier(); // needed to initialize list

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // Declare send queue
        Channel replyChannel = connection.createChannel();
        replyChannel.queueDeclare(SEND_EXCHANGE, false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8"); // for logging purposes
            System.out.println(" [x] Received '" + message + "'");

            SupplyRequest supplyRequest = (SupplyRequest) serializer.classFromString(message, SupplyRequest.class);


            Integer dTime = getSupplyTime(supplyRequest.getProductCode());
            if(dTime != null) {
                SupplyReply supplyReply = new SupplyReply(supplyRequest.getProductCode(), supplyRequest.getOrderId(), dTime, SUPPLIER_NAME);
                replyChannel.basicPublish("", SEND_EXCHANGE, null, serializer.classToString(supplyReply).getBytes());
                System.out.println(" [x] Replied with following deliveryTime: '" + dTime + "'");
            }


        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });


    }

    private static Integer getSupplyTime(String productCode) {
        if(inventory.containsKey(productCode)) {
            Integer deliveryTime = inventory.get(productCode);
            return deliveryTime;
        } else {
            return null;
        }
    }
}

