package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.VideoCategory;
import com.yuyue.app.api.service.HomePageService;
import com.yuyue.app.utils.ResultJSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "homePage", produces = "application/json; charset=UTF-8")
public class HomePageController {
    @Autowired
    private HomePageService homePageService;

    private ReturnResult returnResult=new ReturnResult();

    @ResponseBody
    @RequestMapping("result")
    public JSONObject homePage(){
        List<Banner> banners = homePageService.getBanner();
        List<VideoCategory> categories=homePageService.getVideoCategory();
        Map<String,List> map= Maps.newHashMap();
        map.put("banners",banners);
        map.put("categories",categories);
        returnResult.setResult(JSONObject.toJSON(map));
        returnResult.setMessage("返回轮播图和节目表演成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}
