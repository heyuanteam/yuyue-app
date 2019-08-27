package com.yuyue.app.api.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: Lucifer
 * @create: 2018-12-04 16:18
 * @description:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
//  文件名
    private String filesName;
//  标题
    private String title;
//  节目视频分类
    private String categoryId;
//  时间
    private String uploadTime;
//  视频第一帧图片链接
    private String vedioAddress;
//  文件地址
    private String filesPath;
//  文件类型
    private String filesType;
//  文件大小
//    private String fileSize;
//    作者iD
    private String authorId;
//    描述
    private String description;
//    评论量
    private String commentAmount;
//    点赞量
    private String likeAmount;
//    关注量
    private String attentionAmount;
//    时长
//    private String duration;
}
