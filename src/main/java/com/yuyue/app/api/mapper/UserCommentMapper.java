package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserCommentMapper extends MyBaseMapper<UserComment> {

    /*
      评论做分页
    @Select("SELECT comment.ID id,comment.TEXT text,.comment.VIDEO_ID videoId,comment.USER_ID userId," +
            "comment.CREATE_TIME createTime ,user.USER_NICK_NAME userName,user.HEADP_URL headUrl " +
            "FROM yuyue_user_comment comment left join yuyue_merchant user on comment.user_id = user.id " +
            "where VIDEO_ID = #{videoId} ORDER BY comment.CREATE_TIME desc limit #{began} ,#{size}")
    List<UserCommentVo> getAllComment(@Param(value = "videoId") String videoId, @Param("began") int began, @Param("size") int size);*/
    @Select("SELECT comment.ID id,comment.TEXT text,.comment.VIDEO_ID videoId,comment.USER_ID userId," +
            "DATE_FORMAT(comment.CREATE_TIME,'%Y-%m-%d %H:%i:%s') createTime ,user.USER_NICK_NAME userName,user.HEADP_URL headUrl " +
            "FROM yuyue_user_comment comment left join yuyue_merchant user on comment.user_id = user.id " +
            "where VIDEO_ID = #{videoId} ORDER BY comment.CREATE_TIME desc ")
    List<UserCommentVo> getAllComment(@Param(value = "videoId") String videoId);
    @Transactional
    @Insert("INSERT into yuyue_user_comment (ID,VIDEO_ID,USER_ID,TEXT) " +
            "VALUES (#{id}, #{videoId}, #{userId}, #{text})")
    void addComment(UserComment comment );

    @Transactional
    @Delete("DELETE FROM yuyue_user_comment WHERE ID = #{id}")
    void deleteComment(String id);

}
