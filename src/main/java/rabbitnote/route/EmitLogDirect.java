package rabbitnote.route;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogDirect {
    public final static String host = "10.0.33.145";
    public final static String exchange = "logdirect";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchange, "direct");

        String[] msgType = { "ERROR", "WARN", "INFO", "DEBUG" };

        for (String type : msgType) {
            Random rand = new Random();
            Integer value = rand.nextInt(100);
            String msg = "[" + type + "]" + "Hello world " + value;
            channel.basicPublish(exchange, type, false, false, null, msg.getBytes());
            System.out.println("[x] send:" + msg);
        }
        channel.close();
        connection.close();

    }

}
