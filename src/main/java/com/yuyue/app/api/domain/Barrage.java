package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Barrage implements Serializable {
    private static final long serialVersionUID = 1L;
    //弹幕id
    private String barrageId;
    //视频id
    private String videoId;
    //弹幕内容
    private String text;
    //用户id
    private String userId;
    //用户名
    private String userName;
    //用户头像
    private String userHeadUrl;
    //评论视频的时间点
    private String timePoint;
    //创建时间
    private String createTime;
}
