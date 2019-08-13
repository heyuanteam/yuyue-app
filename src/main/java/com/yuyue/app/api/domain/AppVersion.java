package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppVersion implements Serializable {
    private String appVersionId;
    private String systemType;
    private String versionNo;
    private Date createTime;
    private Date updateTime;
    private String updateUser;
    private String status;
    private String programDescription;
}
