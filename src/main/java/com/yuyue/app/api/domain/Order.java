package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
//    订单号
    private String orderNo;
//    交易类型
    private String tradeType;
//    交易金额
    private String money;
//    手机号
    private String modle;
//    交易状态
    private String status;
//    字典状态code
    private String statusCode;
//    交易时间
    private String createTime;
//    完成时间
    private String completeTime;
//    备注
    private String note;
//    支付返回码
    private String responseCode;
//    支付返回详情
    private String responseMessage;
//    商户ID
    private String merchantId;

}
