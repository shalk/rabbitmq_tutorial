package rabbitnote.topics;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {

    public final static String host = "10.0.33.145";
    public final static String exchange = "hello5";
    
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.exchangeDeclare(exchange, "topic");
        
        String msg = null;
        String routekey = null;
        if( args.length == 2) {
            msg = args[0];
            routekey = args[1];
        } else {
            System.exit(1);
        }
        
        channel.basicPublish(exchange, routekey, null, msg.getBytes());
        System.out.println("exchange:" + exchange);
        System.out.println("routekey:" + routekey);
        System.out.println("[x] send:" + msg);
        
        
    }
}
