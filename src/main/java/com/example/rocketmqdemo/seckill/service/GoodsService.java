package com.example.rocketmqdemo.seckill.service;

import com.example.rocketmqdemo.seckill.domain.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rocketmqdemo.seckill.domain.OrderDto;

/**
* @author lucas
* @description 针对表【goods】的数据库操作Service
* @createDate 2024-05-08 17:01:25
*/
public interface GoodsService extends IService<Goods> {


    /**
     * 真正扣减库存
     * @param orderDto
     */
    void readReduceStock(OrderDto orderDto);
}
