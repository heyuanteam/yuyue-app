package com.yuyue.app.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallAddress implements Serializable {
    private static final long serialVersionUID = 1L;
    //地址id
    private String addressId;
    //下单人id
    private String userId;
    //详细地址
    private String specificAddr;
    //收货人
    private String receiver;
    //手机号
    private String phone;
    //右边
    private String zipCode;
    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //地址id
    private String defaultAddr;





}
