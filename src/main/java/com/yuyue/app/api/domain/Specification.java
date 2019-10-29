package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specification implements Serializable {
    private static final long serialVersionUID = 1L;
    //  商品规格id
    private String commodityId;
    //  商铺id
    private String shopId;
    //  商品规格    描述
    private String commodityDetail;
    //  商品规格    价格
    private BigDecimal commodityPrice;
    //  商品规格    库存
    private int commodityReserve;
    //  商品规格    图片路径
    private String imagePath;
    //  商品规格    状态（是否上架）
    private String status;
    //创建时间
    private String createTime;


}
