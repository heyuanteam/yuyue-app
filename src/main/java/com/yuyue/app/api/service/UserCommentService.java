package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Attention;
import com.yuyue.app.api.domain.Like;
import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;

import java.util.List;

public interface UserCommentService {

    List<UserCommentVo> getAllComment(String videoId ,String userId,int begin,int limit);

    UserComment getUserCommentById(String commentId);

    void addComment(UserComment comment);


    void deleteComment(String id,String videoId);


    /**
     * 查询用户所有的关注
     * @param userId
     * @return
     */
     List<Attention> getUserAttention(String userId,int begin,int limit);
    /**
     * 获取分页所有评论
     * @param videoId
     * @param pageSize
     * @return
     */
     List<UserCommentVo> getCommentByPage(String videoId,int pageSize);

    /**
     *获取视频评论总数
     * @param videoId
     * @return
     */
    int getCommentTotal(String videoId);
    /**
     * 添加关注
     * @param authorId
     * @return
     */
     void addAttention(String id,String userId,String authorId);

    /**
     * 删除用户关注
     * @param authorId
     * @return
     */
     void cancelAttention(String userId,String authorId);

    /**
     * 获取用户关注状态
     * @param userId
     * @param authorId
     * @return
     */
     String getAttentionStatus(String userId,String authorId);


    /**
     * 获取粉丝数
     * @param authorId
     * @return
     */
    int getFansSum(String authorId);

    /**
     * 获取用户点赞状态
     * @return
     */
    String getLikeStatus(String userId,String videoId);

    /**
     * 用户点赞视频
     * @param
     * @param videoId
     */
    void insertToLikeList( String userId,String authorId, String videoId);

    /**
     *作者查看点赞列表信息
     * @param id
     * @param
     * @return
     */
    List<Like> getLikeList(String id);



}
