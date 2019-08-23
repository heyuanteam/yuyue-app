package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;

import java.util.List;

public interface UserCommentService {
    List<UserCommentVo> getAllComment(String videoId );


    List<UserCommentVo> addComment(UserComment comment ,String videoId);


    List<UserCommentVo> deleteComment(String id,String videoId);




}
