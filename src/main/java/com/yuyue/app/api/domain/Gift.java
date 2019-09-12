package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gift implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
//    礼物图片url
    private String giftType;
//    礼物的价值（值多少钻石）
    private BigDecimal giftValue;
//    礼物名称
    private String remark;

}
