package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.SiteShow;
import com.yuyue.app.api.domain.YuyueSite;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YuyueSiteShowMapper extends MyBaseMapper<SiteShow> {

    @Select("SELECT id,showName,category,showPersons,showTime,siteId FROM yuyue_site_show WHERE siteId = #{siteId}")
    List<SiteShow> getShow(String siteId);


}
