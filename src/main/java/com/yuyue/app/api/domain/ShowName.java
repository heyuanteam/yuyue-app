package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 演出申请类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowName implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String createTime;
//    用户ID
    private String userId;
//    姓名或团队名称
    private String teamName;
//    人数
    private String size;
//    现住地
    private String address;
//    身份证正面
    private String cardZUrl;
//    身份证反面
    private String cardFUrl;
//    分类ID
    private String categoryId;
//    节目名称
    private String description;
//    手机
    private String phone;
//    视频地址
    private String videoAddress;
//    邮箱
    private String mail;
//    微信
    private String weChat;

}
