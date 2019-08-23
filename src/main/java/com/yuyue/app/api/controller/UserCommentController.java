package com.yuyue.app.api.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.UserComment;
import com.yuyue.app.api.domain.UserCommentVo;
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

    private ReturnResult returnResult =new ReturnResult();
    private Map<String,Object> map= Maps.newHashMap();

    /**
     * 获取视频中所有的评论
     * @param videoId
     * @return
     */
    @RequestMapping("getAllComment")
    @ResponseBody
    public JSONObject getAllComment(String videoId,String page) {
        List<UserCommentVo> userCommentList = null;
        //设置缓存
        if (StringUtils.isEmpty(page)) page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        if (redisUtil.existsKey("comment" + videoId)) {
            userCommentList = JSON.parseObject((String) redisUtil.getString("comment" + videoId),
                    new TypeReference<List<UserCommentVo>>() {
                    });
            for (UserCommentVo user : userCommentList) {
                System.out.println("redis缓存取出的数据" + user);
            }
        } else {
            userCommentList = userCommentService.getAllComment(videoId);
            String str = JSON.toJSONString(userCommentList);
            redisUtil.setString("comment" + videoId, str, 600);
            System.out.println("查询数据库并存储redis---->>>>>>>" + str);
        } if(userCommentList.isEmpty()) {
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






}
