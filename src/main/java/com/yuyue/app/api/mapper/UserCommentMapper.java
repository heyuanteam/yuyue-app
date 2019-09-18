package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    /**
     * 通过用户id,或视频id 获取所有评论
     * @param videoId
     * @param userId
     * @return
     */
    @Select("SELECT comment.ID as id,comment.TEXT as text,comment.VIDEO_ID as videoId,comment.USER_ID as userId," +
            "DATE_FORMAT(comment.CREATE_TIME,'%Y-%m-%d %H:%i:%s') as createTime ,user.USER_NICK_NAME as userName,user.HEADP_URL as  headUrl " +
            "FROM yuyue_user_comment as comment left join yuyue_merchant as user on comment.user_id = user.id " +
            "where VIDEO_ID = #{videoId} or USER_ID = #{userId} ORDER BY comment.CREATE_TIME desc ")
    List<UserCommentVo> getAllComment(@Param(value = "videoId") String videoId,@Param(value = "userId") String userId);
    /**
     * 评论分页通过视频id 获取所有评论
     * @param videoId
     * @param pageSize
     * @return
     */
    @Select("SELECT comment.ID as id,comment.TEXT as text,comment.VIDEO_ID as videoId,comment.USER_ID as userId," +
            "DATE_FORMAT(comment.CREATE_TIME,'%Y-%m-%d %H:%i:%s') as createTime ,user.USER_NICK_NAME as userName,user.HEADP_URL as  headUrl " +
            "FROM yuyue_user_comment as comment left join yuyue_merchant as user on comment.user_id = user.id " +
            "where VIDEO_ID = #{videoId}  ORDER BY comment.CREATE_TIME desc limit #{pageSize},5")
    List<UserCommentVo> getCommentByPage(@Param(value = "videoId") String videoId,@Param(value = "pageSize") int pageSize);

    @Select("SELECT COUNT(*)\n" +
            "FROM yuyue_user_comment  \n" +
            "where VIDEO_ID = #{videoId}  ")
    int getCommentTotal(@Param(value = "videoId") String videoId);

    @Transactional
    @Insert("replace into yuyue_user_comment (ID,VIDEO_ID,USER_ID,TEXT) " +
            "VALUES (#{id}, #{videoId}, #{userId}, #{text})")
    void addComment(UserComment comment );

    @Transactional
    @Delete("DELETE FROM yuyue_user_comment WHERE ID = #{id}")
    void deleteComment(String id);



}
