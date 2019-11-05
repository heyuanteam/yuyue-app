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
public class MallComment implements Serializable {
    private static final long serialVersionUID = 1L;

    //评论id
    private String commentId;
    //消费者id
    private String consumerId;
    //  商品id
    private String shopId;
    //内容
    private String  content;
    //评分
    private double  score;
    //商品规格
    private String  commoditySize;
    //评论时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //评价人
    private AppUser appUser;

}
