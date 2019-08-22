package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String videoId;
    private String userId;
    private String userName;
    private String headUrl;
    private String text;
    private int commentNum;
    private String createTime;
    private String score;

}
