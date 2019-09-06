package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.YuyueSite;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YuyueSiteMapper extends MyBaseMapper<YuyueSite> {

    @Select("SELECT *,DATE_FORMAT(ADMISSION_TIME,'%Y-%m-%d %H:%i:%s') admissionTime," +
            "DATE_FORMAT(START_TIME,'%Y-%m-%d %H:%i:%s') startTime " +
            "FROM yuyue_site WHERE id = #{id}")
    YuyueSite getSite(String id);

    @Select("SELECT *,DATE_FORMAT(ADMISSION_TIME,'%Y-%m-%d %H:%i:%s') admissionTime," +
            "DATE_FORMAT(START_TIME,'%Y-%m-%d %H:%i:%s') startTime " +
            " FROM yuyue_site ")
    List<YuyueSite> getSiteList();
}
