package com.example.rocketmqdemo;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RocketmqDemoApplicationTests {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Test
    void testSendMessage() {
        rocketMqTemplate.syncSend("test-topic1", "hello,rocketmq");
    }

    /**
     * 测试负载均衡类型消费
     */
    @Test
    void testSendMessage1() {
        for (int i = 1; i <= 10; i++) {
            rocketMqTemplate.syncSend("mode-topic", "我是消息" + i);
        }
    }
}
