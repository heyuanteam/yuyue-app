package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YuyueSite implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    //现场标题
    private String title;
    //图片地址
    private String imageUrl;
    //现场地址
    private String siteAddr;
    //现场总人数
    private String personTotal;
    //申请人数
    private String personSum;
    //入场时间
    private String admissionTime;
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //结束时间
    private String status;







}
