package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppVersion implements Serializable {
    private String appVersionId;
    private String systemType;
    private String versionNo;
    private String createTime;
    private String updateTime;
    private String updateUser;
    private String status;
    private String programDescription;
}
