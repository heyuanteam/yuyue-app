package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.HomePageService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author ly
 */
@RestController
@RequestMapping(value = "homePage", produces = "application/json; charset=UTF-8")
public class HomePageController {
    private static Logger log = LoggerFactory.getLogger(HomePageController.class);

    @Autowired
    private HomePageService homePageService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UploadFileService uploadFileService;

    private ReturnResult returnResult=new ReturnResult();
    private Map<String,List> map= Maps.newHashMap();

    /**
     * 首页展示轮播图及视频种类
     */
    @ResponseBody
    @RequestMapping("result")
    public JSONObject homePage(){
        List<Banner> banners=null;
        List<VideoCategory> categories=null;
        if (redisUtil.existsKey("newbanners") && redisUtil.existsKey("newcategories")){
            banners=JSON.parseObject((String)redisUtil.getString("newbanners" ),
                    new TypeReference<List<Banner>>() {});
            categories =JSON.parseObject((String)redisUtil.getString("newcategories" ),
                    new TypeReference<List<VideoCategory>>() {});
            System.out.println("------redis缓存中取出数据-------");
        } else {
            banners = homePageService.getBanner();
            redisUtil.setString("newbanners", JSON.toJSONString(banners),6000);
            categories=homePageService.getVideoCategory();
            redisUtil.setString("newcategories",JSON.toJSONString(categories));
        }
        map.put("banners",banners);
        map.put("categories",categories);
        returnResult.setResult(JSONObject.toJSON(map));
        returnResult.setMessage("返回轮播图和节目表演成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取首页视频列表
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping("getVideo")
    public JSONObject getVideo(String page){
        List<UploadFileVo> list = Lists.newArrayList();
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 5;
        int begin = (Integer.parseInt(page) - 1) * limit;
        List<UploadFileVo> vdeio_0 = uploadFileService.getVdeio("yuyue_upload_file_0",begin, limit);
        List<UploadFileVo> vdeio_1 = uploadFileService.getVdeio("yuyue_upload_file_1",begin, limit);
        Iterator<UploadFileVo> iterator_0 = vdeio_0.iterator();
        while(iterator_0.hasNext()) {
            list.add(iterator_0.next());
        }
        Iterator<UploadFileVo> iterator_1 = vdeio_1.iterator();
        while(iterator_1.hasNext()) {
            list.add(iterator_1.next());
        }
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无视频！");
        } else {
            map.put("uploadFile", list);
            returnResult.setMessage("视频请求成功！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(JSONObject.toJSON(map));
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取定位，省市区
     *
     * @return
     */
    @RequestMapping("/getCity")
    @ResponseBody
    public JSONObject getCity() {
        List<Address> list = new ArrayList<>();
        if (redisUtil.existsKey("shengshiqu")) {
            list = JSON.parseObject((String) redisUtil.getString("shengshiqu"),
                    new TypeReference<List<Address>>() {});
            log.info("缓存获取省市区");
        } else {
            list = homePageService.getAddress();
            redisUtil.setString("shengshiqu", JSON.toJSONString(list), 60*60*24*30);
        }
        returnResult.setMessage("获取成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONArray.parseArray(JSON.toJSONString(list)));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
}
