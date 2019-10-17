package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.YuyueSite;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface YuyueSiteMapper extends MyBaseMapper<YuyueSite> {

    @Select("SELECT id,title,imageUrl,siteAddr,personTotal,personSum,DATE_FORMAT(ADMISSION_TIME,'%Y-%m-%d %H:%i:%s') admissionTime," +
            "DATE_FORMAT(START_TIME,'%Y-%m-%d %H:%i:%s') startTime ,DATE_FORMAT(END_TIME,'%Y-%m-%d %H:%i:%s') endTime FROM yuyue_site WHERE id = #{id} ")
    YuyueSite getSite(String id);

    @Select("SELECT *,DATE_FORMAT(ADMISSION_TIME,'%Y-%m-%d %H:%i:%s') admissionTime," +
            "DATE_FORMAT(START_TIME,'%Y-%m-%d %H:%i:%s') startTime " +
            " FROM yuyue_site  where  status = '10A' OR status = '10B' limit #{begin},#{limit}")
    List<YuyueSite> getSiteList(@Param(value = "begin") Integer begin, @Param(value = "limit") Integer limit);

    @Transactional
    @Update("UPDATE yuyue_site set personSum=personSum+1 WHERE id = #{id}")
    void updateSite(String id);
}
