package com.example.rocketmqdemo.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zhaojingchao
 * @Date 2024/05/08 18:15
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long goodsId;
    private Long userId;
}
