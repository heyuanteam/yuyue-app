package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Video;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VideoMapper extends MyBaseMapper<Video> {
    @Transactional
    @Insert("INSERT into yuyue_video (id,title,size,uploadTime,authorId,url,description,playAmount,likeAmount,category,duration) " +
            "VALUES (#{id}, #{title}, #{size}, #{uploadTime}, #{authorId}, #{url}, #{description}, #{playAmount}, #{likeAmount}, #{category},#{duration} )")
    void addVideo(Video video);
}
