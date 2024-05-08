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
import org.springframework.stereotype.Component;


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

    /**
     * 真正扣减库存
     * 创建订单数据
     * @param message
     */
    @Override
    public void onMessage(MessageExt message) {
        String orderInfo = new String(message.getBody());
        log.info("监听到的库存数据：{}", orderInfo);
        OrderDto orderDto = JSON.parseObject(orderInfo, OrderDto.class);
        synchronized (this) {
            goodsService.readReduceStock(orderDto);
        }
    }
}
