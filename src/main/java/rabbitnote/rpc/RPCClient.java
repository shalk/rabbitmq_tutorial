package rabbitnote.rpc;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

public class RPCClient {

    public final static String host = "10.0.33.145";
    
    public final static String rpcQueue = "rpc_queue";
    // 简单起见这里用默认的exchange

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String fab = "10";
        channel.queueDeclare(rpcQueue, false, false, false, null);
        String replyQueueName = channel.queueDeclare().getQueue();
        String correlationId = UUID.randomUUID().toString();
        System.out.println("Request Queue:" + rpcQueue);
        System.out.println("correlationId:" + correlationId);
        System.out.println("ReplyTo Queue:" + replyQueueName);

        BasicProperties requestProps = new BasicProperties.Builder().replyTo(replyQueueName)
                .correlationId(correlationId).build();
        channel.basicPublish("", rpcQueue, requestProps, fab.getBytes());
        System.out.println("ask for Fab(" + fab + ")");
        
        BlockingQueue<String> resultQueue = new ArrayBlockingQueue<>(1);
        // wait for result;
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTaq, Envelope envelop, BasicProperties prop, byte[] body) throws IOException {
                
                if (!prop.getCorrelationId().equals(correlationId)){
                    // 确认ID是对应的
                    return;
                }

                String result = new String(body, "UTF-8");
                try {
                    resultQueue.put(result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 手动确认
                    channel.basicAck(envelop.getDeliveryTag(), false);
                }
                
            }
        };
        channel.basicConsume(replyQueueName, false, consumer);
        try {
            String result = resultQueue.take();
            System.out.println("result:" + result);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.println("获取结果超时");
            e.printStackTrace();
        }
        channel.close();
        connection.close();
    }
}
