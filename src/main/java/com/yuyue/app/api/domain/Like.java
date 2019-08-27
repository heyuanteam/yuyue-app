package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    //视频id
    private String videoId;
    //视频名称
    private String videoTittle;
    //用户id
    private String userId;
    //作者id
    private String authorId;
    //用户名
    private String userName;
    //用户头像
    private String headUrl;
    //点赞时间
    private String createTime;
    //点赞状态
    private String status;

}
