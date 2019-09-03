package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.YuyueSite;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YuyueSiteMapper extends MyBaseMapper<YuyueSite> {

    @Select("SELECT * FROM yuyue_site WHERE id = #{id}")
    YuyueSite getSite(String id);

    @Select("SELECT * FROM yuyue_site ")
    List<YuyueSite> getSiteList();
}
