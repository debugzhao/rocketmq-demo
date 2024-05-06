package com.example.rocketmqdemo.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * @Author zhaojingchao
 * @Date 2024/05/06 11:40
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
public class SimpleTest {

    /**
     * name server地址
     */
    private static final String NAMESRV_ADDR = "127.0.0.1:9876";

    /**
     * 发送单向消息
     */
    @Test
    public void testOneWayProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test-oneway-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();
        Message message = new Message("test-oneway-topic", "2024-05-06 17:18:39 日志数据xxxxx".getBytes());
        producer.sendOneway(message);
        producer.shutdown();
        log.info("单向消息发送结束");
    }

    /**
     * 生产者测试
     * @throws MQClientException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     */
    @Test
    public void testProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        // 创建生产者，并指定生产者组
        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        // 配置name server地址
        producer.setNamesrvAddr(NAMESRV_ADDR);
        // 启动生产者
        producer.start();
        // 创建消息
        for (int i = 11; i < 20; i++) {
            Message message = new Message("test-topic", ("测试消息" + i).getBytes());
            SendResult sendResult = producer.send(message);
            log.info("消费发送状态：{}", sendResult.getSendStatus());
        }
        // 关闭生产者
        producer.shutdown();
    }

    /**
     * 测试异步消息
     */
    @Test
    public void testAsyncProducer() throws MQClientException, RemotingException, InterruptedException, IOException {
        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();
        Message message = new Message("async-topic", "异步消息".getBytes());
        producer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("异步消息发送失败：{}", throwable.getMessage());
            }
        });
        log.info("我先执行...");
        System.in.read();
    }

    /**
     * 消费者测试
     * @throws MQClientException
     * @throws IOException
     */
    @Test
    public void testConsumer() throws MQClientException, IOException {
        // 创建消费者，并指定消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-group");
        // 配置name server地址
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        // 订阅主题，*表示订阅这个主题内所有消息
        consumer.subscribe("test-oneway-topic", "*");
        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                // 这里写具体的消费业务逻辑
                log.info("消息扩展内容：{}", list.get(0).toString());
                log.info("消息内容：{}", new String(list.get(0).getBody()));
                log.info("消费上下文:{}", context.toString());
                // CONSUME_SUCCESS：消费成功，消息从队列中移除
                // RECONSUME_LATER：消费失败，消息从队列中保留，过一会儿从队列中重新消费
                return CONSUME_SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();
        System.in.read();
    }
}
