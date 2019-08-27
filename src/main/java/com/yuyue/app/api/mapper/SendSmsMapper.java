package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.JPush;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SendSmsMapper extends MyBaseMapper<JPush> {

    @Select("SELECT * FROM yuyue_jpush b WHERE b.valid = '10A' ")
    List<JPush> getValid();

    @Update("UPDATE yuyue_jpush c SET c.valid = #{status} WHERE c.id = #{id} ")
    void updateValid(@Param("status") String status,@Param("id") String id);
}
