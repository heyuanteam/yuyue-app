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
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderDetail  implements Serializable {
    private static final long serialVersionUID = 1L;



    //订单id
    private String orderId;
    //订单号
    private String orderNo;
    //支付类型
    private String tradeType;
    //下单时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //店家地址
    private String merchantAddr;
    //店家联系电话
    private String merchantPhone;

    //收货地址
    private MallAddress mallAddress;
    //商品列表
    private List<Specification> commodities;
    //商品总额
    private BigDecimal payAmount;
    //运费
    private BigDecimal fare;
    //发货状态（订单状态）
    private String status;




}
