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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<UserCommentVo> getAllComment(String videoId,String userId,int begin,int limit) {
        return userCommentMapper.getAllComment(videoId,userId,begin,limit);
    }

    @Override
    public UserComment getUserCommentById(String commentId) {
        return userCommentMapper.getUserCommentById(commentId);
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
    public List<Attention> getUserAttention(String userId,int begin,int limit) {
        return userAttentionMapper.getUserAttention(userId,begin,limit);
    }

    @Override
    public void addAttention(String id,String userId,String authorId) {
        System.out.println(id+"---------"+userId+"-----------"+authorId);
         userAttentionMapper.addAttention(id,userId,authorId);
         userAttentionMapper.userAttentionAmount(authorId);
    }

    @Override
    public void cancelAttention(String userId,String authorId) {
        //删除关注
        userAttentionMapper.cancelAttention(userId,authorId);
        //关注量  -1
        userAttentionMapper.reduceAttentionAmount(authorId);
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
    public void insertToLikeList(String userId,String authorId, String videoId) {

        UploadFile uploadFile = uploadFileService.fileDetail(authorId,videoId);
        //获取用户信息
        AppUser appUserMsg = loginService.getAppUserMsg("", "", userId);
        Like like=new Like();
        like.setAuthorId(uploadFile.getAuthorId());
        like.setVideoTittle(uploadFile.getFilesName());
        like.setVideoId(videoId);
        //将用户的头像，昵称存入
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
