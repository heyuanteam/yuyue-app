package com.yuyue.app.api.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AppVersion implements Serializable {
    private String appVersionId;
    private String systemType;
    private String versionNo;
    private Date createTime;
    private String updateUser;
    private String status;
    private String programDescription;
}
