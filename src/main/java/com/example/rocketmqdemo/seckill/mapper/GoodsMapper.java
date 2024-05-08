package com.example.rocketmqdemo.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rocketmqdemo.seckill.domain.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
* @author lucas
* @description 针对表【goods】的数据库操作Mapper
* @createDate 2024-05-08 17:01:25
* @Entity com.example.rocketmqdemo.seckill.domain.Goods
*/
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

}




