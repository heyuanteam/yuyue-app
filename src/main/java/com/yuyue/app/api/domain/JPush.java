package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JPush implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
//    通知内容标题
    private String  notificationTitle;
//    消息内容标题
    private String  msgTitle;
//    消息内容
    private String  msgContent;
//    扩展字段
    private String  extras;
//    是否有效
    private String  valid;
    private String createTime;

}
