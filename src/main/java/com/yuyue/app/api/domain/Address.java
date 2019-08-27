package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
//    区域名字
    private String divisionName;
//    上级ID
    private String parentId;
//    区域代码
    private String divisionCode;
//    大区id
    private String chinaRegionId;
//    地区代码
    private String areaCode;

}
