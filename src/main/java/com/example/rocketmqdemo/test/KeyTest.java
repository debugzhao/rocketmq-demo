package com.example.rocketmqdemo.test;

import com.example.rocketmqdemo.dto.OrderDto;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * @Author zhaojingchao
 * @Date 2024/05/07 11:06
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
public class KeyTest {

    private static final String NAMESRV_ADDR = "127.0.0.1:9876";

    @Test
    public void testMessageKey() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String key = UUID.randomUUID().toString();
        log.info("message key:{}", key);
        Message message = new Message("key-topic", "tag1", key, "hello world".getBytes());
        producer.send(message);
        producer.shutdown();
    }

    @Test
    public void testKeyConsumer() throws MQClientException, IOException {
        // 创建消费者，并指定消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-group");
        // 配置name server地址
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        // 订阅主题，*表示订阅这个主题内所有消息
        consumer.subscribe("key-topic", "tag1");
        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                // 这里写具体的消费业务逻辑
                MessageExt messageExt = list.get(0);
                log.info("消息key：{}", messageExt.getKeys());
                log.info("消息内容：{}", new String(messageExt.getBody()));
                return CONSUME_SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();
        System.in.read();
    }
}
