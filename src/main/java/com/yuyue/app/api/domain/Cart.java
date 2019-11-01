package com.yuyue.app.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    //购物车id
    private String cartId;
    //消费者id
    private String consumerId;
    //商品id
    private String commodityId;
    //商铺id
    private String shopId;
    //商品名
    private String commodityName;
    //商品规格
    private String commoditySize;
    //商品价格
    private BigDecimal commodityPrice;
    //商品数量
    private Integer commodityNum;
    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //支付状态
    private String payStatus;


}
