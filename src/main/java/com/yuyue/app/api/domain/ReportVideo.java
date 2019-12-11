package com.yuyue.app.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportVideo implements Serializable {
    private String id;
    //举报人id
    private String userId;
    //作者id
    private String authorId;
    //视频id
    private String videoId;
    //举报内容
    private String content;
    //联系方式
    private String contact;
    //举报的图片
    private String imagePath;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //添加状态
    private String status;

}
