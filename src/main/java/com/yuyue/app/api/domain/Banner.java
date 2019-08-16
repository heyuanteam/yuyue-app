package com.yuyue.app.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banner implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String uploadTime;
    private String url;
    private String description;
    private String status;
    private int sort;

}
