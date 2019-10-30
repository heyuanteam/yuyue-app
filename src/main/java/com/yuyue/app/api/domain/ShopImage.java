package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopImage implements Serializable {
    private static final long serialVersionUID = 1L;

    //  商品id    描述
    private String shopId;
    //  商品图片路径
    private String imagePath;
    //  商品图片排序
    private Byte imageSort;

}
