package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.VideoCategory;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoCategoryMapper extends MyBaseMapper<VideoCategory> {
    @Select("SELECT id,category ,upload_time uploadTime,url,description,category_no categoryNo FROM yuyue_category ORDER BY category_no")
    List<VideoCategory> getVideoCategory();

}
