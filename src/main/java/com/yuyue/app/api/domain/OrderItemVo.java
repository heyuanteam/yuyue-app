package com.yuyue.app.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVo extends OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    //消费者名
    private String consumerName;
    //消费者电话
    private String consumerPhone;
    //订单号
    private String orderNo;
    //订单号
    private String orderId;
    //支付类型
    private String tradeType;
    //顾客收货地址
    private MallAddress mallAddress;
}
