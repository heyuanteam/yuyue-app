package com.yuyue.app.enums;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * action响应基础bean
 */
@Getter
@Setter
@ToString
public class ReturnResult implements Serializable {
    private static final long serialVersionUID = 1L;

//    private String appVersion = "yuyue-app-a-1.0.1";  //APP版本号（默认：yuyue-app-1.0.1）
    private String code = "00";        //（默认：00，没有跳转操作，01跳转）
    private String message = "";     //返回操作提示（如，成功或者失败详情原因）
    private Boolean status = false;     //返回执行结构（如，成功或失败,默认：false）
    private Object result = null;     //返回结果（如，有就是具体参数，没有就是object）
    private String token="";           //返回token值
    private String tomcat = "yuyue-app";  //服务器指定的地址
}
