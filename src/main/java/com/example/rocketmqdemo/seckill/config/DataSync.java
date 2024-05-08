package com.example.rocketmqdemo.seckill.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.rocketmqdemo.seckill.domain.Goods;
import com.example.rocketmqdemo.seckill.mapper.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * @Author zhaojingchao
 * @Date 2024/05/08 17:39
 * @Email zhaojingchao@joysuch.com
 * @Desc 数据同步
 */
@Component
@Slf4j
public class DataSync {


    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void initData() {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        if (goods.isEmpty()) {
            return;
        }
        log.info("开始同步商品数据：{}", JSON.toJSONString(goods));
        for (Goods good : goods) {
            redisTemplate.opsForValue().set("goods:stock:" + good.getId(), good.getStocks());
        }
    }
}
