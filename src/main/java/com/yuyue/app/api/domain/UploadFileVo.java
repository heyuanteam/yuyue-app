package com.yuyue.app.api.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UploadFileVo extends UploadFile implements Serializable {
    private static final long serialVersionUID = 1L;

    //评论数
    private String count;
}
