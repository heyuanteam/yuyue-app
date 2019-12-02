package com.yuyue.app.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Distance implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
//    距离
    private BigDecimal distanceValue;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

}
