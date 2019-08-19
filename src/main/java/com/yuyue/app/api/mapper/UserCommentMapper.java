package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.Video;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserCommentMapper extends MyBaseMapper<Video> {


    @Select("SELECT * FROM yuyue_user_comment WHERE VIDEO_ID = #{videoId}")
    List<UserComment> getAllComment(String videoId);

    @Transactional
    @Insert("INSERT into yuyue_user_comment (ID,VIDEO_ID,USER_ID,USER_NAME,USER_HEAD_URL,TEXT,CREATE_TIME,SCORE) " +
            "VALUES (#{id}, #{videoId}, #{userId}, #{userName}, #{headUrl}, #{text}, #{createTime}, #{score} )")
    void addComment(UserComment comment );

    @Transactional
    @Delete("DELETE FROM yuyue_user_comment WHERE ID = #{id}")
    void deleteComment(String id);

    @Transactional
    @Update("UPDATE yuyue_video SET likeAmount = likeAmount  +  1  WHERE id = #{id}")
    void likeCount(String id);
}
