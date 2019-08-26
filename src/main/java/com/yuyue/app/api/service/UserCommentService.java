package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Attention;
import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;

import java.util.List;

public interface UserCommentService {
    List<UserCommentVo> getAllComment(String videoId );


    List<UserCommentVo> addComment(UserComment comment ,String videoId);


    List<UserCommentVo> deleteComment(String id,String videoId);


    /**
     * 查询用户所有的关注
     * @param userId
     * @return
     */
    public List<Attention> getUserAttention(String userId);

    /**
     * 添加关注
     * @param authorId
     * @return
     */
    public void addAttention(String id,String userId,String authorId);

    /**
     * 删除用户关注
     * @param authorId
     * @return
     */
    public void cancelAttention(String userId,String authorId);




}
