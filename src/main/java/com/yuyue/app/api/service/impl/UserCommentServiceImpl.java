package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.mapper.LikeMapper;
import com.yuyue.app.api.mapper.UserAttentionMapper;
import com.yuyue.app.api.mapper.UserCommentMapper;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.api.service.UserCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service(value = "UserCommentService")
public class UserCommentServiceImpl implements UserCommentService{

    @Autowired
    private UserCommentMapper userCommentMapper;
    @Autowired
    private UserAttentionMapper userAttentionMapper;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private LikeMapper likeMapper;

    /**
     * 获取所有评论
     * @param videoId
     * @param userId
     * @return
     */
    @Override
    public List<UserCommentVo> getAllComment(String videoId,String userId) {
        return userCommentMapper.getAllComment(videoId,userId);
    }
    /**
     * 获取分页所有评论
     * @param videoId
     * @param pageSize
     * @return
     */
    @Override
    public List<UserCommentVo> getCommentByPage(String videoId,int pageSize) {
        return userCommentMapper.getCommentByPage(videoId,pageSize);
    }

    /**
     *获取视频评论总数
     * @param videoId
     * @return
     */
    public int getCommentTotal(String videoId){
        return userCommentMapper.getCommentTotal(videoId);
    }
    @Override
    public void addComment(UserComment comment) {
        userCommentMapper.addComment(comment);
    }

    @Override
    public void deleteComment(String id,String videoId) {
        userCommentMapper.deleteComment(id);
    }
    /**
     * 获取用户关注状态
     * @param userId
     * @param authorId
     * @return
     */
    public String getAttentionStatus(String userId,String authorId){
        return userAttentionMapper.getAttentionStatus(userId,authorId);
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

    @Override
    public int getFansSum( String authorId) {
        return userAttentionMapper.getFansSum(authorId);
    }

    @Override
    public String getLikeStatus(String id, String videoId) {
        return likeMapper.getLikeStatus(id,videoId);
    }


    @Override
    public void insertToLikeList(String userId,String authirId, String videoId) {

        UploadFile uploadFile = uploadFileService.fileDetail(authirId,videoId);
        AppUser appUserMsg = loginService.getAppUserMsg("", "", userId);
        Like like=new Like();
        like.setAuthorId(uploadFile.getAuthorId());
        like.setVideoTittle(uploadFile.getFilesName());
        like.setVideoId(videoId);
        like.setHeadUrl(appUserMsg.getHeadpUrl());
        like.setUserId(userId);
        like.setUserName(appUserMsg.getNickName());
        like.setId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        like.setStatus("1");
        likeMapper.insertToLikeList(like);

    }

    @Override
    public List<Like> getLikeList(String id) {
        return likeMapper.getLikeList(id);
    }

}
