package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.VideoCategory;
import com.yuyue.app.api.service.HomePageService;
import com.yuyue.app.utils.ResultJSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping(value = "homePage", produces = "application/json; charset=UTF-8")
public class HomePageController {
    @Autowired
    private HomePageService homePageService;

    private ReturnResult returnResult=new ReturnResult();

    @RequestMapping("getBanner")
    @ResponseBody
    public JSONObject getBanner() throws ParseException {
        List<Banner> banners = homePageService.getBanner();
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(banners);
        returnResult.setMessage("轮播图返回成功");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    @ResponseBody
    @RequestMapping("getVideoCategory")
    public JSONObject getVideoCategory(){
        List<VideoCategory> videoControllers=homePageService.getVideoCategory();
        returnResult.setResult(JSONObject.toJSON(videoControllers));
        returnResult.setMessage("视频分类返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }



}
