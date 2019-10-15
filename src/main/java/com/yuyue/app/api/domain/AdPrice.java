package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdPrice implements Serializable {
    private static final long serialVersionUID = 1L;
    private String priceId;
    //配置类型
    private String adDuration;
    //类型码
    private String adTotalPrice;
    //配置状态
    private String adDiscount;
    //创建时间
    private String createTime;
    //价格名
    private String adPriceName;
}
