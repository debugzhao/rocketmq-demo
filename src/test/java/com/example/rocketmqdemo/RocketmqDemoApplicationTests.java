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
}
