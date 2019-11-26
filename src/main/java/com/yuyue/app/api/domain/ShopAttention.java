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
public class ShopAttention implements Serializable {
    private static final long serialVersionUID = 1L;
    //id
    private String id;
    //用户id
    private String userId;
    //商户id
    private String shopId;
    //关注状态 1 已关注
    private String status;
    //关注时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
