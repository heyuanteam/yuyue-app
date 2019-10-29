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
public class MallShop implements Serializable {
    private static final long serialVersionUID = 1L;
    //    商品id
    private String shopId;
    //    商家id
    private String merchantId;
    //    商品服务种类
    private String category;
    //    商品/服务名称
    private String commodityName;
    //    上传图片
    private List<ShopImage> images;
    //    商品/服务介绍
    private String detail;
    //    规格
    private List<Specification> specifications;
    //    服务方式
    private String serviceType;
    //    运费
    private BigDecimal fare;
    //    营业时间
    private String businessTime;
    //    商家地址
    private String merchantAddr;
    //    商家电话
    private String merchantPhone;
    //    服务区域
    private String serviceArea;
    //    收费区域
    private String feeArea;
    //    上传视频
    private String videoPath;
    //    备注
    private String remark;
    //    创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
