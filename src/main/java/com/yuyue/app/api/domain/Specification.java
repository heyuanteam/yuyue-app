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
public class Specification implements Serializable {
    private static final long serialVersionUID = 1L;
    //  商品规格id
    private String commodityId;
    //  商铺id
    private String shopId;
    //  商品规格    描述
    private String commodityDetail;
    //  商铺规格    规格/尺寸
    private String commoditySize;
    //  商品规格    价格
    private BigDecimal commodityPrice;
    //  商品规格    库存
    private int commodityReserve;
    //  商品规格    图片路径
    private String imagePath;
    //  商品规格    状态（是否上架）
    private String status;
    //购买的数量
    private Integer commodityNum;
    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //是否评论
    private Boolean isComment;


}
