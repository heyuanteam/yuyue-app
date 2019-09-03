package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteShow implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    //表演节目名称
    private String showName;
    //表演种类
    private String category;
    //表演人员
    private String showPersons;
    //上场时间
    private String showTime;
    //现场id
    private String siteId;
}
