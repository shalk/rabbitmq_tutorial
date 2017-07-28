package rabbitnote.pubsub;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Publisher {
    
    public final static String host = "10.0.33.145";
//    public final static String queue = "hello3";
    public final static String exchange = "subpub";
    
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory  = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.exchangeDeclare(exchange, "fanout", true, false, false, null);
        
        String msg = "Hello All";
        
        channel.basicPublish(exchange, "", null, msg.getBytes());
        
        System.out.println("broadcast:" + msg);
        channel.close();
        connection.close();

    }
}
