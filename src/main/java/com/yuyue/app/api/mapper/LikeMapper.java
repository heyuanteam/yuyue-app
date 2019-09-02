package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.Like;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeMapper extends MyBaseMapper<Like> {

    @Select("SELECT STATUS FROM yuyue_like_list WHERE USER_ID = #{userId} and VIDEO_ID = #{videoId}")
    String getLikeStatus(@Param("userId")String userId,@Param("videoId")String videoId);

    @Insert("INSERT INTO yuyue_like_list   (ID,VIDEO_ID,USER_ID,AUTHOR_ID,USER_NAME,VIDEO_NAME,HEADP_URL,STATUS)  " +
            "VALUES  (#{id},#{videoId},#{userId},#{authorId},#{userName},#{videoTittle},#{headUrl},#{status})")
    void insertToLikeList(Like like);

    @Select("SELECT ID id,VIDEO_ID videoId,USER_ID userId,AUTHOR_ID authorId, USER_NAME userName,VIDEO_NAME videoTittle,HEADP_URL headUrl," +
            "STATUS  status, DATE_FORMAT(CREATE_TIME ,'%Y-%m-%d %H:%i:%s') createTime  FROM yuyue_like_list WHERE AUTHOR_ID = #{id}")
    List<Like> getLikeList(String id);
}
