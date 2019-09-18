package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.YuyueSitePerson;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface YuyueSitePersonMapper extends MyBaseMapper<YuyueSitePerson>{
    @Select("SELECT ID id,SITE_ID siteId,USER_ID userId,USER_REAL_NAME userRealName,STATUS status," +
            "DATE_FORMAT(CREATE_TIME,'%Y-%m-%d %H:%i:%s') createTime " +
            "FROM yuyue_site_person " +
            "WHERE USER_ID = #{userId} and SITE_ID = #{siteId}")
    YuyueSitePerson getSitePerson(@Param(value = "userId") String userId, @Param(value = "siteId") String siteId);

    @Insert("REPLACE INTO yuyue_site_person (ID,SITE_ID,USER_ID,USER_REAL_NAME) VALUES (#{id},#{siteId},#{userId},#{userRealName})")
    void addSitePerson(YuyueSitePerson yuyueSitePerson);

}
