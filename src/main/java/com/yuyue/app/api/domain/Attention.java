package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attention implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
    private String  authorId;
    private String  userId;
    private String  createTime;
    private String  status;

}
