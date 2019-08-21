package com.yuyue.app.api.controller;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UserCommentService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequestMapping(value="userComment", produces = "application/json; charset=UTF-8")
@RestController
public class UserCommentController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(UploadFileController.class);

    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LoginService loginService;

    private ReturnResult returnResult =new ReturnResult();
    private Map<String,List> map= Maps.newHashMap();

    @RequestMapping("getAllComment")
    @ResponseBody
    public JSONObject getAllComment(String videoId) {
        List<UserComment> userCommentList =null;
        //设置缓存
        if(redisUtil.existsKey(videoId)){
            userCommentList=(List<UserComment>)(Object)redisUtil.getList(videoId, 0, -1);
            for (Object  user: userCommentList
            ) {
                System.out.println("redis缓存取出的数据"+user);
            }
        }else {
            userCommentList = userCommentService.getAllComment(videoId);
            System.out.println("查询数据库并存储redis");
            redisUtil.setListAll(videoId,userCommentList,600);
        }
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONObject.toJSON(userCommentList));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    @RequestMapping("addComment")
    @ResponseBody
    public JSONObject addComment(HttpServletRequest request) {
        Map<String, String> mapValue = getParameterMap(request);
        if (StringUtils.isEmpty(mapValue.get("userId")) || StringUtils.isEmpty(mapValue.get("videoId"))) {
            returnResult.setMessage("用户id为空！或视频id为空！");
        } else {
            System.out.println(mapValue.get("userId")+"----->"+mapValue.get("videoId")+"------>"+mapValue.get("text"));
            UserComment comment=new UserComment();
            String id= UUID.randomUUID().toString().replace("-","").toUpperCase();
            comment.setId(id);
            comment.setVideoId(mapValue.get("videoId"));
            comment.setUserId(mapValue.get("userId"));
            AppUser appUser = loginService.getAppUserById(mapValue.get("userId"));
            comment.setUserName(appUser.getNickName());
            comment.setText(mapValue.get("text"));
            comment.setHeadUrl(appUser.getHeadpUrl());
            comment.setScore("0");
            List<UserComment> comments =userCommentService.addComment(comment,mapValue.get("videoId"));
            if(CollectionUtils.isEmpty(comments)){
                returnResult.setMessage("暂无评论！");
            } else {
                map.put("comment",comments);
                returnResult.setMessage("评论成功！");
                returnResult.setStatus(Boolean.TRUE);
                returnResult.setResult(JSONObject.toJSON(map));
            }
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    @RequestMapping("deleteComment")
    @ResponseBody
    public JSONObject deleteComment(HttpServletRequest request) {
        Map<String, String> mapValue = getParameterMap(request);
        List<UserComment> userComments=userCommentService.deleteComment(mapValue.get("id"),mapValue.get("videoId"));
        map.put("comment",userComments);
        returnResult.setMessage("删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONObject.toJSON(map));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }





}
