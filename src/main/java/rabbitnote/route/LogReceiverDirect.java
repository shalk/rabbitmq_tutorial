package rabbitnote.route;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class LogReceiverDirect {
    
    public final static String host = "10.0.33.145";
    public final static String exchange = "logdirect";
    
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.exchangeDeclare(exchange, "direct");
        
        String queueName = channel.queueDeclare().getQueue();
        
        String routekey = "";
        if ( args.length == 1) {
            routekey = args[0];
        } else {
            System.exit(1);
        }

        channel.queueBind(queueName, exchange, routekey);
        System.out.println("queue:"+ queueName);
        System.out.println("routekey:"+ routekey);
        
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envolope, AMQP.BasicProperties props, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("[x] revc:" + msg);
            }
        };

        channel.basicConsume(queueName, true, consumer);
        
        
    }

}
