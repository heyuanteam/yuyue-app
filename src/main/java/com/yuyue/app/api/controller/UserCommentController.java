package com.yuyue.app.api.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.api.service.UserCommentService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 获取视频中所有的评论
     * @param videoId
     * @return
     */
    @RequestMapping("/getAllComment")
    @ResponseBody
    public JSONObject getAllComment(String videoId,String page, HttpServletRequest request) {
        log.info("获取视频中所有的评论-------------->>/userComment/getAllComment");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        if(videoId.isEmpty()){
            returnResult.setMessage("视频id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Map<String,Object> map= Maps.newTreeMap();
        List<UserCommentVo> userCommentList = null;
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        //设置缓存
        if (redisUtil.existsKey("comment" + videoId)) {
            userCommentList = JSON.parseObject((String) redisUtil.getString("comment" + videoId),
                    new TypeReference<List<UserCommentVo>>() {});
//            for (UserCommentVo user : userCommentList) {
//                System.out.println("redis缓存取出的数据" + user);
//            }
        } else {
            userCommentList = userCommentService.getAllComment(videoId,"",begin,limit);
            String str = JSON.toJSONString(userCommentList);
            redisUtil.setString("comment" + videoId, str, 60);
//            System.out.println("查询数据库并存储redis---->>>>>>>" + str);
        }
        if(userCommentList.isEmpty()) {
            returnResult.setMessage("暂无评论！");
        }else {
            returnResult.setMessage("返回成功！");
        }
        map.put("comment", userCommentList);
        map.put("commentNum", userCommentService.getCommentTotal(videoId));
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(map);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    @RequestMapping("/test")
    @ResponseBody
    public void test(){
        ValueOperations<String, String> valueTemplate = redisTemplate.opsForValue();
        Gson gson = new Gson();

        valueTemplate.set("StringKey1", "hello spring boot redis, String Redis");
        String value = valueTemplate.get("StringKey1");
        System.out.println(value);

        valueTemplate.set("StringKey2", gson.toJson(new Person("theName", 11)));
        Person person = gson.fromJson(valueTemplate.get("StringKey2"), Person.class);
        System.out.println(person);
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
        log.info("用户添加评论-------------->>/userComment/addComment");
        getParameterMap(request);
        Map<String, String> mapValue = getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        String videoId=mapValue.get("videoId");
        String authorId=mapValue.get("authorId");
        Map<String,Object> map= Maps.newTreeMap();
        if(authorId.isEmpty() || videoId.isEmpty() || user.getId().isEmpty()){
            returnResult.setMessage("作者id或视频id不能为空!!");
        } else {
            UserComment comment=new UserComment();
            String id= UUID.randomUUID().toString().replace("-","").toUpperCase();
            comment.setId(id);
            comment.setVideoId(videoId);
            comment.setUserId(user.getId());
            comment.setText(mapValue.get("text"));
            //普通用户，商人  评论
            if (!"2".equals(user.getUserType()) && !"4".equals(user.getUserType())){
                uploadFileService.allRoleCommentAmount(authorId,videoId,user.getId());
            }
            //艺人评论   用户表，视频表评论数+1
            else {
                uploadFileService.allRoleCommentAmount(authorId,videoId,"");
            }
            //数据插入到Comment表中
            userCommentService.addComment(comment);
            //获取所有评论
            List<UserCommentVo> allComment = userCommentService.getAllComment(videoId, "",1,10);
            //获取评论数
            //int commentTotal = userCommentService.getCommentTotal(videoId);
            returnResult.setMessage("评论成功！");
            map.put("comment", allComment);
            map.put("commentNum",userCommentService.getCommentTotal(videoId));
            returnResult.setResult(map);
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
        log.info("用户删除评论-------------->>/userComment/deleteComment");
        ReturnResult returnResult =new ReturnResult();
        Map<String, String> mapValue = getParameterMap(request);
        String commentId=mapValue.get("id");
        String videoId=mapValue.get("videoId");
        String authorId=mapValue.get("authorId");
        if(authorId.isEmpty() || videoId.isEmpty() || user.getId().isEmpty()){
            returnResult.setMessage("作者id或视频id不能为空!!");
        }else {
            if (!"2".equals(user.getUserType()) && !"4".equals(user.getUserType())){
                uploadFileService.reduceCommentAmount(authorId,videoId,user.getId());
            }
            //艺人评论   用户表，视频表评论数+1
            else {
                uploadFileService.reduceCommentAmount(authorId,videoId,"");
            }
            //通过评论id查询是否存在此评论
            userCommentService.deleteComment(commentId,videoId);

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
    public JSONObject getUserAttention(@CurrentUser AppUser user,String content,String page ,HttpServletRequest request){
        log.info("查询用户所有的关注-------------->>/userComment/getUserAttention");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        List<AppUser> appUserList= Lists.newArrayList();
//        System.out.println("---------"+user.getId());
        List<Attention> userAttentions = userCommentService.getUserAttention(user.getId(),begin,limit);

        if(userAttentions.isEmpty()){
            returnResult.setResult(userAttentions);
            returnResult.setMessage("该用户没有关注！！");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if (StringUtils.isNotEmpty(content)){
            appUserList = loginService.getAppUserMsgToLike(user.getId(), content);
            if (appUserList.isEmpty()){
                returnResult.setResult(userAttentions);
                returnResult.setMessage("查无此人！！");
                returnResult.setStatus(Boolean.TRUE);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }else {
            for (Attention attention:userAttentions
            ) {
                AppUser appUserMsg = loginService.getAppUserMsg("", "", attention.getAuthorId());
                appUserList.add(appUserMsg);
            }
        }
        returnResult.setResult(appUserList);
        returnResult.setMessage("返回成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * (我的发布)通过艺人id获取视频
     * @param user
     * @param authorId
     * @param request
     * @return
     */
    @RequestMapping("/getVideoByAuthorId")
    @ResponseBody
    @LoginRequired
    public JSONObject getVideoByAuthorId(@CurrentUser AppUser user,String authorId,String page, HttpServletRequest request){
        log.info("通过艺人id获取视频-------------->>/userComment/getVideoByAuthorId");
        getParameterMap(request);
        Map<String,Object> map= Maps.newTreeMap();
        ReturnResult returnResult =new ReturnResult();
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        if(authorId.isEmpty()  || user.getId().isEmpty()){
            returnResult.setMessage("作者id不能为空!!");
        }
        List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthor(authorId,begin,limit);
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
    public JSONObject addAttention(@CurrentUser AppUser user,String authorId, HttpServletRequest request){
        log.info("添加关注-------------->>/userComment/addAttention");
        getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        if(authorId.isEmpty()  || user.getId().isEmpty()){
            returnResult.setMessage("作者id不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if(authorId.equals(user.getId())){
            returnResult.setMessage("不能关注自己！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        List<Attention> userAttention = userCommentService.getUserAttention(user.getId(),1,10);
        for (Attention attertion:userAttention) {
            if (attertion.getAuthorId().equals(authorId)){
                returnResult.setMessage("用户已关注！！");
                returnResult.setStatus(Boolean.TRUE);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }

        //用户表中的关注数据+1  ;  数据添加至Attention表中
        String id =UUID.randomUUID().toString().replace("-","").toUpperCase();
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
    public JSONObject cancelAttention(@CurrentUser AppUser user,String authorId, HttpServletRequest request){
        log.info("取消用户关注-------------->>/userComment/cancelAttention");
        getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        if(authorId.isEmpty()  || user.getId().isEmpty()){
            returnResult.setMessage("作者id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        String attentionStatus = userCommentService.getAttentionStatus(user.getId(), authorId);

        if(StringUtils.isEmpty(attentionStatus)){
            returnResult.setMessage("未关注！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }//关注表删除数据  ; 用户表 关注量-1
        userCommentService.cancelAttention(user.getId(),authorId);
        returnResult.setMessage("取消关注成功！！");
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
    public JSONObject getFansSum(@CurrentUser AppUser user, HttpServletRequest request){
        log.info("获取用户粉丝量-------------->>/userComment/getFansSum");
        getParameterMap(request);
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
    public JSONObject insertToLikeList(@CurrentUser AppUser user,String authorId,String videoId, HttpServletRequest request){
        log.info("用户添加视频点赞-------------->>/userComment/insertToLikeList");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        if(authorId.isEmpty() || videoId.isEmpty() ){
            returnResult.setMessage("作者id或视频id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        String likeStatus = userCommentService.getLikeStatus(user.getId(), videoId);
//        System.out.println("---------------------"+likeStatus);
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
    public JSONObject getLikeList(@CurrentUser AppUser user, HttpServletRequest request){
        log.info("作者查看点赞列表信息-------------->>/userComment/getLikeList");
        getParameterMap(request);
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
