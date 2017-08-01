package rabbitnote.rpc;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RPCServer {

    public final static String host = "10.0.33.145";
    
    public final static String rpcQueue = "rpc_queue";
    public static int fab(int i) {
        if (i == 0){
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        return fab(i-2) + fab(i-1);
    }
    
    public static void main(String args[]) throws IOException, TimeoutException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.basicQos(1);
        channel.queueDeclare(rpcQueue, false, false, false, null);
        
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties props, byte[] body) throws IOException {
                
                String fabNum = new String(body, "UTF-8");
                String correlationId = props.getCorrelationId();
                String replyQueue = props.getReplyTo();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties().builder().correlationId(correlationId).build();
                System.out.println("[x] recv:" + fabNum);
                System.out.println("[x] correlaiton:" + correlationId);
                System.out.println("[x] replyQueue:" + replyQueue);
                
                String reply = "";
                // 处理参数
                Integer fabInt ;
                try{
                    fabInt = Integer.parseInt(fabNum);
                    System.out.println("fabInt:" + fabInt);
                    reply = String.valueOf(fab(fabInt));
                }catch(Exception e) {
                    System.out.println("解析fabNum失败" + fabNum);
                    e.printStackTrace();
                    reply = e.getMessage();
                }finally{
                   channel.basicAck(envelope.getDeliveryTag(), false); 
                   channel.basicPublish("", replyQueue, replyProps, reply.getBytes());
                   System.out.println("[x] reply:" + reply);
                }
            }
        };
        channel.basicConsume(rpcQueue, false, consumer);
    }
}
