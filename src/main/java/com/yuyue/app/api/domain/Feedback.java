package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户反馈类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String createTime;
//    联系方式
    private String contact;
//    图片链接
    private String pictureUrl;
//    问题详情
    private String details;
//    处理状态
    private String status;
//    用户ID
    private String userId;
}
