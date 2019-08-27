package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String videoId;
    private String vedeoTitile;
    private String userId;
    private String authorId;
    private String userName;
    private String headUrl;
    private String createTime;

}
