package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YuyueSitePerson implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    //用户id
    private String userId;
    //用户真实姓名
    private String userRealName;
    //现场id
    private String siteId;
    //入场状态
    private String status;
    //创建时间
    private String createTime;
}
