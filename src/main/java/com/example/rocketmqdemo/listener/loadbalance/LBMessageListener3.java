package com.example.rocketmqdemo.listener.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author zhaojingchao
 * @Date 2024/05/07 17:54
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "mode-group-a",
        topic = "mode-topic",
        messageModel = MessageModel.CLUSTERING // 负载均衡模式
)
public class LBMessageListener3 implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt message) {
        log.info("消费者组：{} 第3个消费者，消费内容：{}", "mode-group-a", new String(message.getBody()));
    }
}
