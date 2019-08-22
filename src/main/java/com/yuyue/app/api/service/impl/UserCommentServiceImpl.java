package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;
import com.yuyue.app.api.mapper.UserCommentMapper;
import com.yuyue.app.api.service.UserCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class UserCommentServiceImpl implements UserCommentService{

    @Autowired
    private UserCommentMapper userCommentMapper;

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

}
