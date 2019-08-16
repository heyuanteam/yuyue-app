package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Banner;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerMapper extends MyBaseMapper<Banner> {
    @Select("SELECT b.id,b.`name`,b.url,b.description,b.status,b.sort,DATE_FORMAT(b.uploadTime ,'%Y-%m-%d %H:%i:%s') uploadTime " +
            "FROM yuyue_banner b WHERE b.`status` = '1' ORDER BY b.sort")
    List<Banner> getBanner();
}
