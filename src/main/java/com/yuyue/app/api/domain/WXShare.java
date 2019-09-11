package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WXShare implements Serializable {
    private static final long serialVersionUID = 1L;
 /*   private String wxNoncestr;
    private long wxTimestamp;
    private String wxSignature;*/


    private String access_token;
    private String ticket;
    private String noncestr;
    private String timestamp;
    private String appId;
    private String signature;


    public void setAttr(String text,WXShare wxShare){

    }
    public void setAttr(String text,String content){

    }

}
