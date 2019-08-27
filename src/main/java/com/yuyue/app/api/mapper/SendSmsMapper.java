package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.JPush;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SendSmsMapper extends MyBaseMapper<JPush> {

    @Select("")
    List<JPush> getValid();
}
