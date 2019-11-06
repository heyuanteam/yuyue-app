package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultCart implements Serializable {
    private static final long serialVersionUID = 1L;

    //商铺id
    private String shopId;
    //商品名
    private String commodityName;
    //订单列表
    private List<Cart> commodityList;
    //商品列表
    private List<Specification> commodities;
    //商品总额
    private BigDecimal payAmount;
    //运费
    private BigDecimal fare;



}
