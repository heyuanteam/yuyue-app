package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCategory implements Serializable {
    private String id;
    private String category;
    private String uploadTime;
    private String url;
    private String description;
    private String categoryNo;
}
