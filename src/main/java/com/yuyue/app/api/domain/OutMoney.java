package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutMoney implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
//    提现订单编号
    private String outNo;
//    商户ID
    private String merchantId;
//    交易类型
    private String tradeType;
//    交易金额
    private BigDecimal money;
//    交易状态
    private String status;
//    交易时间
    private String createTime;
//    完成时间
    private String completeTime;
//    支付返回码
    private String responseCode;
//    支付返回详情
    private String responseMessage;
//    转账收款账户号
    private String moneyNumber;
//    账户真实名称
    private String realName;

}
