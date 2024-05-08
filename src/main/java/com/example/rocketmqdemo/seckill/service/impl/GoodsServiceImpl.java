package com.example.rocketmqdemo.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rocketmqdemo.seckill.domain.Goods;
import com.example.rocketmqdemo.seckill.domain.OrderDto;
import com.example.rocketmqdemo.seckill.domain.OrderRecords;
import com.example.rocketmqdemo.seckill.mapper.OrderRecordsMapper;
import com.example.rocketmqdemo.seckill.service.GoodsService;
import com.example.rocketmqdemo.seckill.mapper.GoodsMapper;
import com.example.rocketmqdemo.seckill.service.OrderRecordsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* @author lucas
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2024-05-08 17:01:25
*/
@Slf4j
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>  implements GoodsService {


    @Autowired
    private OrderRecordsService orderRecordsService;


    /**
     * 扣减库存、并且需要生成订单数据（这两个操作需要是原子性的）
     * @param orderDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readReduceStock(OrderDto orderDto) {
        Long goodsId = orderDto.getGoodsId();
        Goods goods = this.baseMapper.selectById(goodsId);
        log.info("消费的商品数据：{}", goods);
        int finalStock = goods.getStocks() - 1;
        if (finalStock < 0) {
            throw new RuntimeException("商品id: " + goodsId + "库存不足, 用户id：" + orderDto.getUserId());
        }
        goods.setStocks(finalStock);
        goods.setUpdateTime(new Date());
        int result = this.baseMapper.updateById(goods);
        if (result > 0) {
            OrderRecords record = new OrderRecords();
            record.setGoodsId(goodsId.intValue());
            record.setUserId(orderDto.getUserId().intValue());
            record.setCreateTime(new Date());
            orderRecordsService.save(record);

            log.info("扣减库存、并且需要生成订单数据成功");
        }
    }
}




