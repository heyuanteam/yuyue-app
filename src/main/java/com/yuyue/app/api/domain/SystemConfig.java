package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    //配置类型
    private String type;
    //类型码
    private String typeCode;
    //配置状态
    private String status;
    //创建时间
    private String createTime;
}
