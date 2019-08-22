package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String videoId;
    private String text;
    private String createTime;
    private String userId;


}
