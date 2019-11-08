package com.yuyue.app.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallShopVo extends MallShop implements Serializable {
    private static final long serialVersionUID = 1L;

    //  商品id    描述
    private String shopId;
    //  商品图片路径
    private String imagePath;
    //  商品图片排序
    private Byte imageSort;
    //  距离
    private Long distance;

}
