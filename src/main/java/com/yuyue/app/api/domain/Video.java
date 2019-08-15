package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String size;
    private Date uploadTime;
    private String authorId;
    private String url;
    private String description;
    private int playAmount;
    private int likeAmount;
    private String category;
    private String duration;
}
