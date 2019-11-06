package com.yuyue.app.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    //      订单项id
    private String orderItemId;
    //      订单id
    private String orderId;
    //      地址id
    private String addressId;
    //      商铺id
    private String shopId;
    //      规格id
    private String commodityId;
    //      消费者id
    private String consumerId;

    //      运费
    private BigDecimal fare;
    //      支付金额
    private BigDecimal commodityPrice;
    //      商品数量
    private int commodityNum;

    //      支付类型及配送状态
    private String status;
    //      创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
