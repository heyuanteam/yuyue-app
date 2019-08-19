package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.mapper.UserCommentMapper;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UserCommentService;
import com.yuyue.app.utils.ResultJSONUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequestMapping("userComment")
@RestController
public class UserCommentController {



    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private LoginService loginService;
    private ReturnResult returnResult =new ReturnResult();
    private Map<String,List> map= new HashMap<>();

    @RequestMapping("getAllComment")
    @ResponseBody
    public List<UserComment> getAllComment(String videoId) {
        return userCommentService.getAllComment(videoId);
    }

    @RequestMapping("addComment")
    @ResponseBody
    public JSONObject addComment(String userId,String videoId,String text) {
        if (userId == null || userId == "" ) {
            returnResult.setMessage("用户id为空！");
            returnResult.setStatus(Boolean.FALSE);
            returnResult.setMessage("");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if(videoId == null || videoId == ""){
            returnResult.setMessage("视频id为空！");
            returnResult.setStatus(Boolean.FALSE);
            returnResult.setMessage("");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        System.out.println(userId+"-------------------->"+videoId+"-------------------->"+text);
        UserComment comment=new UserComment();
        String id= UUID.randomUUID().toString().replace("-","");
        comment.setId(id);
        comment.setVideoId(videoId);
        comment.setUserId(userId);
        AppUser appUser = loginService.getAppUserById(userId);
        System.out.println(appUser);
        comment.setUserName(appUser.getNickName());
        comment.setText(text);
        comment.setCreateTime(new Date());
        comment.setScore("0");
        List<UserComment> userComments=userCommentService.addComment(comment,videoId);
        map.put("comment",userComments);
        returnResult.setMessage("评论成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONObject.toJSON(map));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    @RequestMapping("deleteComment")
    @ResponseBody
    public JSONObject deleteComment(String id,String videoId) {
        List<UserComment> userComments=userCommentService.deleteComment(id,videoId);
        map.put("comment",userComments);
        returnResult.setMessage("删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONObject.toJSON(map));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    @RequestMapping("likeCount")
    @ResponseBody
    public void likeCount(String videoId) {
        userCommentService.likeCount(videoId);
    }



}
