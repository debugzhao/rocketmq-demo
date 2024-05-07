package com.example.rocketmqdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zhaojingchao
 * @Date 2024/05/07 09:45
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String orderNum;
    private Long userId;
    private String desc;
}
