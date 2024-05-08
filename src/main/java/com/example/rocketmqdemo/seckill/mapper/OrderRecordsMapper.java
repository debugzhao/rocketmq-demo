package com.example.rocketmqdemo.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rocketmqdemo.seckill.domain.OrderRecords;
import org.apache.ibatis.annotations.Mapper;

/**
* @author lucas
* @description 针对表【order_records】的数据库操作Mapper
* @createDate 2024-05-08 17:01:25
* @Entity com.example.rocketmqdemo.seckill.domain.OrderRecords
*/
@Mapper
public interface OrderRecordsMapper extends BaseMapper<OrderRecords> {

}




