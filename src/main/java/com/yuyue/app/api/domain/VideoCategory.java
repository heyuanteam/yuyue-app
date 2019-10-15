package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String category;
    private String uploadTime;
    private String url;
    private String description;
    private int categoryNo;
    private String status;
}
