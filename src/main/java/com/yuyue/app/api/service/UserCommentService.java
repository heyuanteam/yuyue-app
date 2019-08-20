package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.UserComment;

import java.util.List;

public interface UserCommentService {
    List<UserComment> getAllComment(String videoId);


    List<UserComment> addComment(UserComment comment ,String videoId);


    List<UserComment> deleteComment(String id,String videoId);




}
