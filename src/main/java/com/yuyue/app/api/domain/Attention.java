package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attention implements Serializable {
    private static final long serialVersionUID = 1L;

    private String  id;
    //视频作者ID  （艺人）
    private String  authorId;
    //普通用户id
    private String  userId;
    //关注时间
    private String  createTime;
    //关注状态
    private String  status;

}
