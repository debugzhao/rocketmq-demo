package com.example.rocketmqdemo.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author zhaojingchao
 * @Date 2024/05/07 16:03
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "test-consumer-group", topic = "test-topic1")
public class MessageListener implements RocketMQListener<MessageExt> {

    /**
     * 消费者消费方法
     * @param message
     */
    @Override
    public void onMessage(MessageExt message) {
        log.info("消息内容：{}", new String(message.getBody()));
    }
}
