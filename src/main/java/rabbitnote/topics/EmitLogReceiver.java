package rabbitnote.topics;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class EmitLogReceiver {
    
    public final static String exchange = "hello5";
    public final static String host = "10.0.33.145";
    
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.exchangeDeclare(exchange, "topic");
        
        String routingKey = null;
        if ( args.length == 1) {
            routingKey = args[0];
        } else {
            System.exit(1);
        }
        
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, exchange, routingKey);
        System.out.println("queueName:" + queueName);
        System.out.println("exchange:" + exchange);
        System.out.println("routingkey:" + routingKey);
        
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties props, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("[x] recieve:" + msg);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
