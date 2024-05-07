package com.example.rocketmqdemo.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
import static org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.RECONSUME_LATER;

/**
 * @Author zhaojingchao
 * @Date 2024/05/07 11:06
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
public class RetryTest {

    private static final String NAMESRV_ADDR = "127.0.0.1:9876";

    @Test
    public void testProducerRetry() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();
        // 设置同步发送重试次数
        producer.setRetryTimesWhenSendFailed(2);
        // 设置异步发送重试次数
        producer.setRetryTimesWhenSendAsyncFailed(2);
        String key = UUID.randomUUID().toString();
        log.info("message key:{}", key);
        Message message = new Message("retry-topic", "tag1", key, "消息222：更加优雅的处理死信消息方案...".getBytes());
        producer.send(message);
        producer.shutdown();
    }

    @Test
    public void testConsumerRetry() throws MQClientException, IOException {
        // 创建消费者，并指定消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-group");
        // 配置name server地址
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        // 订阅主题，*表示订阅这个主题内所有消息
        consumer.subscribe("retry-topic", "tag1");
        // 设置消费者最大重试次数，超过最大重试次数的消息将被投递到死信队列中
        consumer.setMaxReconsumeTimes(2);
        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = list.get(0);
                log.info("时间:{},消息key：{}, 消息内容：{}", LocalDateTime.now(), messageExt.getKeys(), new String(messageExt.getBody()));
                // 业务代码抛异常/返回null/返回RECONSUME_LATER 消费者都会重试消费
                return RECONSUME_LATER;
            }
        });

        // 启动消费者
        consumer.start();
        System.in.read();
    }

    /**
     * 更加优雅的处理死信消息方案
     * @throws MQClientException
     * @throws IOException
     */
    @Test
    public void testConsumerRetry2() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-group");
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        consumer.subscribe("retry-topic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                // 业务代码抛异常/返回null/返回RECONSUME_LATER 消费者都会重试消费
                MessageExt messageExt = list.get(0);
                int reconsumeTimes = messageExt.getReconsumeTimes();
                try {
                    // 处理业务逻辑
                    log.info("消息key：{}, 消息内容：{}", messageExt.getKeys(), new String(messageExt.getBody()));
                    log.info("当前消费者重试次数: {}", reconsumeTimes);
                    int i = 2 / 0;
                } catch (Exception e) {
                    // 获取当前重试次数
                    if (reconsumeTimes > 3) {
                        log.info("消息重试次数超过3次，消息进入死信队列，该消息记录到MySQL中，通知人工处理");
                        return CONSUME_SUCCESS;
                    }
                    return RECONSUME_LATER;
                }
                return CONSUME_SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();
        System.in.read();
    }


    /**
     * 订阅死信队列消息
     * @throws MQClientException
     * @throws IOException
     */
    @Test
    public void testDeadQueueConsumer() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-dlq-group");
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        consumer.subscribe("%DLQ%test-group", "*");
        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = list.get(0);
                log.info("监听到死信消息，记录到文件/MySQL中，或者发邮件通知人工处理。\n 消息内容：{}", new String(messageExt.getBody()));

                // 业务代码抛异常/返回null/返回RECONSUME_LATER 消费者都会重试消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();
        System.in.read();
    }
}
