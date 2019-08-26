package com.yuyue.app.api.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UploadFileService;
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

/**
 * @author ly
 */
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
    @Autowired
    private UploadFileService uploadFileService;

    private ReturnResult returnResult =new ReturnResult();
    private Map<String,Object> map= Maps.newTreeMap();

    /**
     * 获取视频中所有的评论
     * @param videoId
     * @return
     */
    @RequestMapping("getAllComment")
    @ResponseBody
    public JSONObject getAllComment(String videoId) {
        List<UserCommentVo> userCommentList = null;
        //设置缓存
        if (redisUtil.existsKey("comment" + videoId)) {
            userCommentList = JSON.parseObject((String) redisUtil.getString("comment" + videoId),
                    new TypeReference<List<UserCommentVo>>() {});
            for (UserCommentVo user : userCommentList) {
                System.out.println("redis缓存取出的数据" + user);
            }
        } else {
            userCommentList = userCommentService.getAllComment(videoId);
            String str = JSON.toJSONString(userCommentList);
            redisUtil.setString("comment" + videoId, str, 600);
            System.out.println("查询数据库并存储redis---->>>>>>>" + str);
        }
        if(userCommentList.isEmpty()) {
            returnResult.setMessage("暂无评论！");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        map.put("comment", userCommentList);
        map.put("commentNum", userCommentList.size());
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONObject.toJSON(map));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 用户添加评论
     * @param request
     * @return
     */
    @RequestMapping("addComment")
    @ResponseBody
    @LoginRequired
    public JSONObject addComment(HttpServletRequest request,@CurrentUser AppUser user) {
        Map<String, String> mapValue = getParameterMap(request);
        if (StringUtils.isEmpty(user.getId()) || StringUtils.isEmpty(mapValue.get("videoId"))) {
            returnResult.setMessage("用户id为空！或视频id为空！");
        } else {
            UserComment comment=new UserComment();
            String id= UUID.randomUUID().toString().replace("-","").toUpperCase();
            comment.setId(id);
            comment.setVideoId(mapValue.get("videoId"));
            comment.setUserId(user.getId());
            comment.setText(mapValue.get("text"));
            List<UserCommentVo> comments =userCommentService.addComment(comment,mapValue.get("videoId"));
            if(CollectionUtils.isEmpty(comments)){
                returnResult.setMessage("暂无评论！");
            } else {
                map.put("comment",comments);
                map.put("commentNum",comments.size());
                returnResult.setMessage("评论成功！");
                returnResult.setStatus(Boolean.TRUE);
                returnResult.setResult(JSONObject.toJSON(map));
            }
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 用户删除评论
     * @param request
     * @return
     */
    @RequestMapping("deleteComment")
    @ResponseBody
    public JSONObject deleteComment(HttpServletRequest request) {
        Map<String, String> mapValue = getParameterMap(request);
        List<UserCommentVo> userComments=userCommentService.deleteComment(mapValue.get("id"),mapValue.get("videoId"));
        map.put("comment",userComments);
        map.put("commentNum",userComments.size());
        returnResult.setMessage("删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONObject.toJSON(map));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 查询用户所有的关注
     * @param user
     * @return
     */
    @RequestMapping("getUserAttention")
    @ResponseBody
    @LoginRequired
    public JSONObject getUserAttention(@CurrentUser AppUser user){
        Map<String,Object> map= Maps.newTreeMap();
        List<Attention> userAttention = userCommentService.getUserAttention(user.getId());
        if(userAttention.isEmpty()){
            returnResult.setMessage("该用户没有关注！！");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        for (Attention attention: userAttention) {
            System.out.println("作者id:"+attention.getAuthorId());
            AppUser appUserById = loginService.getAppUserMsg("","",attention.getAuthorId());
            List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthorId(attention.getAuthorId());
            if(videoByAuthorId.isEmpty()){
                break;
            }else{
                    appUserById.setAuthorVideo(videoByAuthorId);
                    System.out.println("---------------------");
                    System.out.println(appUserById.getAuthorVideo().size());
                    System.out.println(appUserById);
                    map.put("ID"+appUserById.getId(),appUserById);


                System.out.println("获取作者上传的视频id:"+appUserById.getId()+"视频数："+videoByAuthorId.size());
            }

        }

        returnResult.setResult(map);
        returnResult.setMessage("返回成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 添加关注
     * @param authorId
     * @return
     */
    @RequestMapping("addAttention")
    @ResponseBody
    @LoginRequired
    public JSONObject addAttention(@CurrentUser AppUser user,String authorId){
        List<Attention> userAttention = userCommentService.getUserAttention(user.getId());
        for (Attention attertion:userAttention
             ) {
            if (attertion.getAuthorId().equals(authorId)){
                returnResult.setMessage("用户已关注！！");
                returnResult.setStatus(Boolean.TRUE);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        String id =UUID.randomUUID().toString().replace("-","").toUpperCase();
        userCommentService.addAttention(id,user.getId(),authorId);
        return getUserAttention(user);
    }

    /**
     * 删除用户关注
     * @param user,authorId
     * @return
     */
    @RequestMapping("cancelAttention")
    @ResponseBody
    @LoginRequired
    public JSONObject cancelAttention(@CurrentUser AppUser user,String authorId){
        userCommentService.cancelAttention(user.getId(),authorId);
        return getUserAttention(user);

    }

}
