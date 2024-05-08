package com.example.rocketmqdemo.seckill.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

/**
*
* @TableName order_records
*/
@TableName("order_records")
@Data
public class OrderRecords implements Serializable {

    /**
    *
    */
    @TableId(type = AUTO)
    @NotNull(message="[]不能为空")
    private Integer id;
    /**
    *
    */
    private Integer userId;
    /**
    *
    */
    @Size(max= 255,message="编码长度不能超过255")
    @Length(max= 255,message="编码长度不能超过255")
    private String orderSn;
    /**
    *
    */

    private Integer goodsId;
    /**
    *
    */

    private Date createTime;

}
