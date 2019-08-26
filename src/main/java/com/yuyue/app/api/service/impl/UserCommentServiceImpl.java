package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Attention;
import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;
import com.yuyue.app.api.mapper.UserAttentionMapper;
import com.yuyue.app.api.mapper.UserCommentMapper;
import com.yuyue.app.api.service.UserCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service(value = "UserCommentService")
public class UserCommentServiceImpl implements UserCommentService{

    @Autowired
    private UserCommentMapper userCommentMapper;
    @Autowired
    private UserAttentionMapper userAttentionMapper;

    @Override
    public List<UserCommentVo> getAllComment(String videoId) {
        return userCommentMapper.getAllComment(videoId);
    }

    @Override
    public List<UserCommentVo> addComment(UserComment comment,String videoId) {
        userCommentMapper.addComment(comment);
        return getAllComment(videoId);
    }

    @Override
    public List<UserCommentVo> deleteComment(String id,String videoId) {
        userCommentMapper.deleteComment(id);
        return getAllComment(videoId);
    }

    @Override
    public List<Attention> getUserAttention(String userId) {


        return userAttentionMapper.getUserAttention(userId);
    }

    @Override
    public void addAttention(String id,String userId,String authorId) {
        System.out.println(id+"---------"+userId+"-----------"+authorId);
         userAttentionMapper.addAttention(id,userId,authorId);
    }

    @Override
    public void cancelAttention(String userId,String authorId) {
         userAttentionMapper.cancelAttention(userId,authorId);
    }

}
