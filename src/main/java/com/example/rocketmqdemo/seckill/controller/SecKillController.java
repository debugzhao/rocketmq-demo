package com.example.rocketmqdemo.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.example.rocketmqdemo.seckill.domain.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author zhaojingchao
 * @Date 2024/05/08 16:27
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Slf4j
@RestController
public class SecKillController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private final AtomicLong atomicLong = new AtomicLong(0L);

    /**
     * 订单topic
     */
    private static final String ORDER_TOPIC = "seckill-topic";

    @GetMapping("test3")
    public String test1() {
        return "OK";
    }

    @GetMapping("/secKill")
    public String secKill(Long goodsId) {
        log.info("商品id：{}正在被秒杀", goodsId);
        long userId = atomicLong.getAndIncrement();
        String uniqueKey = "seckill:" + goodsId + ":" + userId;
        log.info("uniqueKey:{}", uniqueKey);
        // 1.去重（先判断该商品是否已经抢购过）
        // setIfAbsent = setnx
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(uniqueKey, " ");
        if (!isAbsent) {
            return "您已经抢购过该商品了";
        }

        // 根据JMM内存模型可知，所有的先查询数据，再修改数据，再更新数据操作都是线程不安全的
        // 所以不能用常规的方式去扣减库存，而是使用redis内部的decrement方法扣减库存
        // 因为redis内部是单线程的，不存在线程安全问题
        // 2.redis预扣减库存
        Long stock = redisTemplate.opsForValue().decrement("goods:stock:" + goodsId);
        if (stock < 0) {
            return "该商品已售完";
        }

        OrderDto orderDto = new OrderDto(goodsId, userId);
        // 3.MQ发送异步消息
        rocketMQTemplate.asyncSend(ORDER_TOPIC, JSON.toJSONString(orderDto), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("异步消息发送失败，异常信息：{}", throwable.getMessage());
                log.error("用户id:{}，商品id:{}", atomicLong, goodsId);
            }
        });
        return "正在拼命抢购中，请稍后再订单中心查询订单信息";
    }
}
