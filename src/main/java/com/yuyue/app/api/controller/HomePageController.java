package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.HomePageService;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * @author ly
 */
@RestController
@RequestMapping(value = "/homePage", produces = "application/json; charset=UTF-8")
public class HomePageController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(HomePageController.class);

    @Autowired
    private HomePageService homePageService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private LoginService loginService;



    /**
     * 首页展示轮播图及视频种类
     */
    @ResponseBody
    @RequestMapping("/result")
    public JSONObject homePage(HttpServletRequest request, HttpServletResponse response){
        log.info("首页展示轮播图及视频种类-------------->>/homePage/result");
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        getParameterMap(request);
        Map<String,List> map= Maps.newHashMap();
        ReturnResult returnResult=new ReturnResult();
        List<Banner> banners=null;
        List<VideoCategory> categories=null;
        if (redisUtil.existsKey("newBanners") && redisUtil.existsKey("newCategories")){
            banners=JSON.parseObject((String)redisUtil.getString("newBanners" ),
                    new TypeReference<List<Banner>>() {});
            categories =JSON.parseObject((String)redisUtil.getString("newCategories" ),
                    new TypeReference<List<VideoCategory>>() {});
            System.out.println("------redis缓存中取出数据-------");
        } else {
            banners = homePageService.getBanner();
            redisUtil.setString("newBanners", JSON.toJSONString(banners),6000);
            categories=homePageService.getVideoCategory();
            redisUtil.setString("newCategories",JSON.toJSONString(categories));
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
    @RequestMapping("/getVideo")
    public JSONObject getVideo(String page,String categoryId,String content,HttpServletRequest request){
        log.info("获取首页视频列表-------------->>/homePage/getVideo");
        getParameterMap(request);
        Map<String,List> map= Maps.newHashMap();
        ReturnResult returnResult=new ReturnResult();
        List<UploadFile> list = Lists.newArrayList();
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 5;
        int begin = (Integer.parseInt(page) - 1) * limit;
   /*     List<UploadFile> vdeio_0 = uploadFileService.getVideo("yuyue_upload_file_0",begin, limit,categoryId);
        List<UploadFile> vdeio_1 = uploadFileService.getVideo("yuyue_upload_file_1",begin, limit,categoryId);*/
        List<UploadFile> uploadFilList0 = uploadFileService.getVideo("yuyue_upload_file_0",begin, limit,categoryId,content);
        List<UploadFile> uploadFileList1 = uploadFileService.getVideo("yuyue_upload_file_1",begin, limit,categoryId,content);

        for (UploadFile uploadFile:uploadFilList0) {
            //视频中插入作者信息
            AppUser appUserMsg = loginService.getAppUserMsg("", "",uploadFile.getAuthorId());
            uploadFile.setAppUser(appUserMsg);
            list.add(uploadFile);
        }
        for (UploadFile uploadFile:uploadFileList1) {
            //视频中插入作者信息
            AppUser appUserMsg = loginService.getAppUserMsg("", "",uploadFile.getAuthorId());
            uploadFile.setAppUser(appUserMsg);
            list.add(uploadFile);
        }
    /*  Iterator<UploadFile> iterator_0 = vdeio_0.iterator();
        while(iterator_0.hasNext()) {
            //视频中插入作者信息
            AppUser appUserMsg = loginService.getAppUserMsg("", "", iterator_0.next().getAuthorId());
            iterator_0.next().setAppUser(appUserMsg);
            list.add(iterator_0.next());
        }
        Iterator<UploadFile> iterator_1 = vdeio_1.iterator();
        while(iterator_1.hasNext()) {
            //视频中插入作者信息
            AppUser appUserMsg = loginService.getAppUserMsg("", "", iterator_1.next().getAuthorId());
            iterator_1.next().setAppUser(appUserMsg);
            list.add(iterator_1.next());
        }*/
        map.put("uploadFile", list);
        returnResult.setResult(map);
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无视频！");
        } else {
            returnResult.setMessage("视频请求成功！");
        }
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取定位，省市区
     *
     * @return
     */
    @RequestMapping("/getCity")
    @ResponseBody
    public JSONObject getCity(HttpServletRequest request) {
        log.info("获取定位，省市区-------------->>/homePage/getCity");
        getParameterMap(request);
        Map<String,List> map= Maps.newHashMap();
        ReturnResult returnResult=new ReturnResult();
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

    /**
     * 获取现场节目
     * @param id
     * @return
     */
    @RequestMapping("/getSite")
    @ResponseBody
    public JSONObject getSite(String id,HttpServletRequest request){
        log.info("获取现场节目-------------->>/homePage/getSite");
        getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        if (StringUtils.isEmpty(id)){
            List<YuyueSite> siteList = homePageService.getSiteList();
            if (StringUtils.isEmpty(siteList)){
                returnResult.setMessage("暂无信息！！");
            }else
                returnResult.setMessage("返回成功！！");
            for (YuyueSite yuyueSite:siteList) {
                System.out.println(yuyueSite);
            }
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(siteList);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            Map<String,Object> map=Maps.newHashMap();
            YuyueSite site = homePageService.getSite(id);
            if (StringUtils.isNull(site)){
                returnResult.setMessage("暂无信息！！");
            }else
                returnResult.setMessage("返回信息！！");
            List<SiteShow> showList = homePageService.getShow(id);
            if (StringUtils.isNull(showList))
                returnResult.setMessage("暂无节目");
            else
                returnResult.setMessage("节目表单返回成功");
            map.put("site",site);
            map.put("showList",showList);
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(map);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

    }
}
