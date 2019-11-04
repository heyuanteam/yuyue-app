package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
    //商品列表
    private List<Cart> commodityList;



}
