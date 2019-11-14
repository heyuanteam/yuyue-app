package com.yuyue.app.api.domain;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class ChangeMoneyVo extends ChangeMoney implements Serializable {
    private static final long serialVersionUID = 1L;

//    艺人名称
    private String sourceName;
//    送礼物的名称
    private String yiName;
//    送礼物的头像
    private String headpUrl;

}
