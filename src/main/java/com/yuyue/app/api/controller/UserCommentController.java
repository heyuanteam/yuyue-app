package com.yuyue.app.api.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
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
 * @author ly   This Controller class provides user attention, likes, comments
 */
@RequestMapping(value="/userComment", produces = "application/json; charset=UTF-8")
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



    /**
     * 获取视频中所有的评论
     * @param videoId
     * @return
     */
    @RequestMapping("/getAllComment")
    @ResponseBody
    public JSONObject getAllComment(String videoId) {
        ReturnResult returnResult =new ReturnResult();
        if(videoId.isEmpty()){
            returnResult.setMessage("视频id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Map<String,Object> map= Maps.newTreeMap();
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
            redisUtil.setString("comment" + videoId, str, 1);
            System.out.println("查询数据库并存储redis---->>>>>>>" + str);
        }
        if(userCommentList.isEmpty()) {
            returnResult.setMessage("暂无评论！");
        }else {
            returnResult.setMessage("返回成功！");
        }
        map.put("comment", userCommentList);
        map.put("commentNum", userCommentList.size());
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(map);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 用户添加评论
     * @param request(videoId,authorId,token)
     * @return
     */
    @RequestMapping("/addComment")
    @ResponseBody
    @LoginRequired
    public JSONObject addComment(HttpServletRequest request,@CurrentUser AppUser user) {
        Map<String, String> mapValue = getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        String videoId=mapValue.get("videoId");
        String authorId=mapValue.get("authorId");
        if(authorId.isEmpty() || videoId.isEmpty() || user.getId().isEmpty()){
            returnResult.setMessage("作者id或视频id不能为空!!");
        } else {
            UserComment comment=new UserComment();
            String id= UUID.randomUUID().toString().replace("-","").toUpperCase();
            comment.setId(id);
            comment.setVideoId(videoId);
            comment.setUserId(user.getId());
            comment.setText(mapValue.get("text"));
            //用户表，视频表评论数+1
            uploadFileService.commentAmount(authorId,videoId);
            //数据插入到Comment表中
            userCommentService.addComment(comment);
            returnResult.setMessage("评论成功！");
            returnResult.setStatus(Boolean.TRUE);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 用户删除评论
     * @param request( 评论id ,作者id ,视频id)
     * @return
     */
    @RequestMapping("/deleteComment")
    @ResponseBody
    @LoginRequired
    public JSONObject deleteComment(HttpServletRequest request,@CurrentUser AppUser user) {
        ReturnResult returnResult =new ReturnResult();
        Map<String, String> mapValue = getParameterMap(request);
        String commentId=mapValue.get("id");
        String videoId=mapValue.get("videoId");
        String authorId=mapValue.get("authorId");
        if(authorId.isEmpty() || videoId.isEmpty() || user.getId().isEmpty()){
            returnResult.setMessage("作者id或视频id不能为空!!");
        }else {
            //通过评论id查询是否存在此评论
            userCommentService.deleteComment(commentId,videoId);
            uploadFileService.reduceCommentAmount(authorId,videoId);
            returnResult.setMessage("删除成功！");
            returnResult.setStatus(Boolean.TRUE);
        }

        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 查询用户所有的关注
     * @param user
     * @return
     */
    @RequestMapping("/getUserAttention")
    @ResponseBody
    @LoginRequired
    public JSONObject getUserAttention(@CurrentUser AppUser user){
        ReturnResult returnResult =new ReturnResult();
        List<AppUser> appUserList= Lists.newArrayList();
        System.out.println("---------"+user.getId());
        List<Attention> userAttentions = userCommentService.getUserAttention(user.getId());
        if(userAttentions.isEmpty()){
            returnResult.setMessage("该用户没有关注！！");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        for (Attention attention:userAttentions
             ) {
            AppUser appUserMsg = loginService.getAppUserMsg("", "", attention.getAuthorId());
            appUserList.add(appUserMsg);
        }
        returnResult.setResult(appUserList);
        returnResult.setMessage("返回成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    @RequestMapping("/getVideoByAuthorId")
    @ResponseBody
    @LoginRequired
    public JSONObject getVideoByAuthorId(@CurrentUser AppUser user,String authorId){
        Map<String,Object> map= Maps.newTreeMap();
        ReturnResult returnResult =new ReturnResult();
        if(authorId.isEmpty()  || user.getId().isEmpty()){
            returnResult.setMessage("作者id不能为空!!");
        }
        List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthorId(authorId);
        if(videoByAuthorId.isEmpty()){
            returnResult.setResult(map);
            returnResult.setMessage("暂无视频！！");
            returnResult.setStatus(Boolean.TRUE);

        }else {
            returnResult.setResult(videoByAuthorId);
            returnResult.setMessage("返回成功！！");
            returnResult.setStatus(Boolean.TRUE);
        }
          return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 添加关注
     * @param authorId
     * @return
     */
    @RequestMapping("/addAttention")
    @ResponseBody
    @LoginRequired
    public JSONObject addAttention(@CurrentUser AppUser user,String authorId){
        ReturnResult returnResult=new ReturnResult();
        List<Attention> userAttention = userCommentService.getUserAttention(user.getId());
        if(authorId.isEmpty()  || user.getId().isEmpty()){
            returnResult.setMessage("作者id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        for (Attention attertion:userAttention
             ) {
            if (attertion.getAuthorId().equals(authorId)){
                returnResult.setMessage("用户已关注！！");
                returnResult.setStatus(Boolean.TRUE);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        String id =UUID.randomUUID().toString().replace("-","").toUpperCase();
        //用户表中的关注数据+1
        uploadFileService.attentionAmount(authorId);
        //数据添加至Attention表中
        userCommentService.addAttention(id,user.getId(),authorId);
        returnResult.setMessage("关注成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 取消用户关注
     * @param user,authorId
     * @return
     */
    @RequestMapping("/cancelAttention")
    @ResponseBody
    @LoginRequired
    public JSONObject cancelAttention(@CurrentUser AppUser user,String authorId){
        ReturnResult returnResult=new ReturnResult();
        if(authorId.isEmpty()  || user.getId().isEmpty()){
            returnResult.setMessage("作者id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        List<Attention> userAttentions = userCommentService.getUserAttention(user.getId());

        if(userAttentions.isEmpty()){
            returnResult.setMessage("暂无关注！！");
        }
        for (Attention attention:userAttentions
             ) {
            if (authorId.equals(attention.getAuthorId())){
                //关注表删除数据
                userCommentService.cancelAttention(user.getId(),authorId);
                //用户表 关注量-1
                uploadFileService.reduceAttentionAmount(authorId);
                returnResult.setMessage("取消关注成功！！");
                returnResult.setStatus(Boolean.TRUE);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        returnResult.setMessage("未关注！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取用户粉丝量
     * @param user
     * @return
     */
    @RequestMapping("/getFansSum")
    @ResponseBody
    @LoginRequired
    public JSONObject getFansSum(@CurrentUser AppUser user){
        ReturnResult returnResult =new ReturnResult();
        userCommentService.getFansSum(user.getId());
        returnResult.setMessage("返回成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);

    }

    /**
     *用户添加视频点赞
     * @param user
     * @param videoId
     * @return
     */
    @RequestMapping("/insertToLikeList")
    @ResponseBody
    @LoginRequired
    public JSONObject insertToLikeList(@CurrentUser AppUser user,String authorId,String videoId){
        ReturnResult returnResult =new ReturnResult();
        if(authorId.isEmpty() || videoId.isEmpty() ){
            returnResult.setMessage("作者id或视频id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        String likeStatus = userCommentService.getLikeStatus(user.getId(), videoId);
        System.out.println("---------------------"+likeStatus);
        if(likeStatus == null || "0".equals(likeStatus)){
            //用户表及视频表中的字段LIKE_TOTAL +1;
            uploadFileService.likeAcount(authorId,videoId);
            //点赞的数据添加至LIKE表中
            userCommentService.insertToLikeList(user.getId(),authorId,videoId);
            returnResult.setMessage("点赞成功");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            returnResult.setMessage("视频已点赞");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
    }
    /**
     *查询用户视频点赞状态
     * @param user
     * @param videoId
     * @return
     */
    /* @RequestMapping("/getLikeStatus")
    @ResponseBody
    @LoginRequired
   public JSONObject getLikeStatus(@CurrentUser AppUser user,String videoId){
        ReturnResult returnResult =new ReturnResult();
        if( videoId.isEmpty() ){
            returnResult.setMessage("视频id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        String likeStatus = userCommentService.getLikeStatus(user.getId(), videoId);
        if(likeStatus == null || "0".equals(likeStatus)){
            returnResult.setMessage("未点赞");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            returnResult.setMessage("已点赞");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
    }
*/


    /**
     *作者查看点赞列表信息
     * @param user
     * @param
     * @return
     */
    @RequestMapping("/getLikeList")
    @ResponseBody
    @LoginRequired
    public JSONObject getLikeList(@CurrentUser AppUser user){
        ReturnResult returnResult =new ReturnResult();
        List<Like> likeList = userCommentService.getLikeList(user.getId());
        if(likeList.isEmpty()){
            returnResult.setMessage("暂无点赞");
        }else {
            returnResult.setResult(likeList);
            returnResult.setMessage("返回成功");
        }
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
}
