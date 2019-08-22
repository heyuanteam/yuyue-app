package com.yuyue.app.api.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserCommentVo extends UserComment  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String headUrl;
}
