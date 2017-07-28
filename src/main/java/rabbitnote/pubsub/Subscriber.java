package rabbitnote.pubsub;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Subscriber {

    public final static String host = "10.0.33.145";
    public final static String exchange = "subpub";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        String queueName = channel.queueDeclare().getQueue();
        System.out.println("Queue:" + queueName);
        channel.queueBind(queueName, exchange, "", null);


        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
                String msg = new String(body,"UTF-8");
                System.out.println("[x] Recv:" + msg);
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}
