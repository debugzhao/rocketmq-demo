package com.example.rocketmqdemo.seckill.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

/**
*
* @TableName goods
*/
@TableName("goods")
@Data
public class Goods implements Serializable {

    /**
    *
    */
    @TableId(type = AUTO)
    @NotNull(message="[]不能为空")
    private Integer id;
    /**
    *
    */
    @Size(max= 255,message="编码长度不能超过255")
    @Length(max= 255,message="编码长度不能超过255")
    private String goodsName;
    /**
    *
    */
    private BigDecimal price;
    /**
    *
    */
    private Integer stocks;
    /**
    *
    */
    private Integer status;
    /**
    *
    */
    @Size(max= 255,message="编码长度不能超过255")
    @Length(max= 255,message="编码长度不能超过255")
    private String pic;
    /**
    *
    */
    private Date createTime;
    /**
    *
    */
    private Date updateTime;

}
