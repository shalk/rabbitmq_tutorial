package rabbitnote.hello;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
    public final static String HOST = "10.0.33.145";
    public final static String QUEUE = "hello1";
    
    public static void main(String[] args) throws IOException, TimeoutException {
       ConnectionFactory factory = new ConnectionFactory(); 
       factory.setHost(HOST);
       Connection connection = factory.newConnection();
       Channel channel = connection.createChannel();
       channel.queueDeclare(QUEUE, false, false, false, null);
       
       String msg = "Hello World!";
       
       for (int i = 0; i < 10 ; i++) {

           String msg1 = msg + i;
           channel.basicPublish("", QUEUE, false, false, null, msg1.getBytes());
           System.out.println("[x] send:" + msg);
       }
       
       channel.close();
       connection.close();
    }
}   