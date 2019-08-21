package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.VideoCategory;
import com.yuyue.app.api.service.HomePageService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/**
 * @author ly
 */
@RestController
@RequestMapping(value = "homePage", produces = "application/json; charset=UTF-8")
public class HomePageController {
    @Autowired
    private HomePageService homePageService;
    @Autowired
    private RedisUtil redisUtil;

    private ReturnResult returnResult=new ReturnResult();
    private Map<String,List> map= Maps.newHashMap();

    @ResponseBody
    @RequestMapping("result")
    /**
     * 首页展示轮播图
     */
    public JSONObject homePage(){
        List<Banner> banners=null;
        List<VideoCategory> categories=null;
        if (redisUtil.existsKey("banners") && redisUtil.existsKey("categories")){
            banners = (List<Banner>)(Object)redisUtil.getList("banners", 0, -1);
            categories = (List<VideoCategory>)(Object)redisUtil.getList("categories", 0, -1);
            System.out.println("------redis缓存中取出数据-------");
        }else {
            banners = homePageService.getBanner();
            redisUtil.setListAll("banners", banners, 86400);
            categories=homePageService.getVideoCategory();
            redisUtil.setList("categories",categories);
        }
        map.put("banners",banners);
        map.put("categories",categories);
        returnResult.setResult(JSONObject.toJSON(map));
        returnResult.setMessage("返回轮播图和节目表演成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
}
