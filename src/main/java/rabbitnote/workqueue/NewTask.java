package rabbitnote.workqueue;

import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

    public final static String host = "10.0.33.145";
    public final static String queue = "hello2";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(queue, true, false, false, null);
        for(int i = 0; i < 10 ; i++) {
            Random rand= new Random();
            String msg = "hehe " + i + " " + new String(new char[rand.nextInt(20)+1]).replace('\0', '.');
            System.out.println("[x] send:" + msg);
            channel.basicPublish("", queue, false, false, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        }
        channel.close();
        connection.close();
    }

}
