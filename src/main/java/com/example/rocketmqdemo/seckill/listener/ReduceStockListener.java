package com.example.rocketmqdemo.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.example.rocketmqdemo.seckill.domain.OrderDto;
import com.example.rocketmqdemo.seckill.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;


/**
 * @Author zhaojingchao
 * @Date 2024/05/08 18:10
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill-topic",
        consumerGroup = "seckill-consumer-group",
        consumeMode = ConsumeMode.CONCURRENTLY, // 消费模式：并发消费
        consumeThreadMax = 40 // 最大消费者线程数量：40
)
public class ReduceStockListener implements RocketMQListener<MessageExt> {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 方案1：先加锁再开始事务，保证线程安全
     * 真正扣减库存
     * 创建订单数据
     * @param message
     */
//    @Override
//    public void onMessage(MessageExt message) {
//        String orderInfo = new String(message.getBody());
//        log.info("监听到的库存数据：{}", orderInfo);
//        OrderDto orderDto = JSON.parseObject(orderInfo, OrderDto.class);
//        synchronized (this) {
//            goodsService.readReduceStock(orderDto);
//        }
//    }

    /**
     * 方案2：利用MySQL行锁实现分布式锁
     * 真正扣减库存
     * 创建订单数据
     * @param message
     */
//    @Override
//    public void onMessage(MessageExt message) {
//        String orderInfo = new String(message.getBody());
//        log.info("监听到的库存数据：{}", orderInfo);
//        OrderDto orderDto = JSON.parseObject(orderInfo, OrderDto.class);
//        goodsService.readReduceStock(orderDto);
//    }


    /**
     * 优化方案3：利用redis setnx命令实现分布式锁，压力分摊到redis和java程序中，缓解db压力
     * @param message
     */
    @Override
    public void onMessage(MessageExt message) {
        String orderInfo = new String(message.getBody());
        log.info("监听到的库存数据：{}", orderInfo);
        OrderDto orderDto = JSON.parseObject(orderInfo, OrderDto.class);

        // while循环进行自旋
        while (true) {
            // 尝试获取锁
            Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock:" + orderDto.getGoodsId(), "", Duration.ofSeconds(10));
            // 拿到锁
            if (flag) {
                // 执行业务代码
                try {
                    goodsService.readReduceStock(orderDto);
                    return;
                } finally {
                    // 执行完业务代码之后，最终还要释放锁，方便其他线程抢占锁
                    redisTemplate.delete("lock:" + orderDto.getGoodsId());
                }
            } else {
                // 没有拿到锁的线程睡眠200ms，防止cpu资源过度消耗。自旋进行下一次抢占锁。
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
