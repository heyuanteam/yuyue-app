package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.BarrageService;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.list.TreeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@RestController
@RequestMapping(value = "/barrage" ,produces = "application/json; charset=UTF-8")
public class BarrageController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(BarrageController.class);

    @Autowired
    private BarrageService barrageService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LoginService loginService;



    /**
     * 获取弹幕信息
     * @param request
     * @return
     */
    @RequestMapping("/getBarrages")
    @ResponseBody
    public JSONObject getBarrages(HttpServletRequest request){
        log.info("获取弹幕信息-------------->>/barrage/getBarrages");
        Map<String,String> mapValue = getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        final String videoId = mapValue.get("videoId");
        final int startTime = Integer.parseInt(mapValue.get("startTime"));
        final int endTime = Integer.parseInt(mapValue.get("endTime"));
        if (StringUtils.isEmpty(videoId)){
            returnResult.setMessage("视频不能为空");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (startTime < 0){
            returnResult.setMessage("时间段：开始时间错误");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (endTime < 0  ||  startTime>endTime){
            returnResult.setMessage("时间段：结束时间错误");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
    /*    List<Barrage> list =null;
        if(redisUtil.existsKey("barrage"+videoId)){
            list=JSON.parseObject((String) redisUtil.getString("barrage" + videoId),
                    new TypeReference<List<Barrage>>() {});
            for (Barrage b: list) {
                System.out.println("--------redis-------"+b);
            }
            //数据与传入的时间做业务处理
        }else {
            list = barrageService.getBarrages(videoId,startTime,endTime);
            redisUtil.setString("barrage"+videoId, JSON.toJSONString(list),60);
            *//*redisUtil.setListAll(videoId,list,6000);*//*
        }
        //数据与传入的时间做业务处理*/
        List<Barrage> list =barrageService.getBarrages(videoId,startTime,endTime);
        Map<Integer,List> map= new TreeMap<>();
        List<String> textList=null;
        for (int i=0;i<list.size();i++){
                if (map.containsKey(list.get(i).getTimePoint()) ){
                    textList.add(list.get(i).getText());
//                    for (String s:textList) {
//                        System.out.println(s);
//                    }
                    map.put(list.get(i).getTimePoint(),textList);
//                    System.out.println("map"+map.keySet());
                }else {
                    /*textList.clear();*/
                    /*textList.removeAll(textList);*/
                    textList = new TreeList();
                    String text = list.get(i).getText();
//                    System.out.println(text);
                    textList.add(text);
                    map.put(list.get(i).getTimePoint(),textList);
//                    for (String s:textList) {
//                        System.out.println("---"+s);
//                    }
//                    System.out.println("map0 key:"+map.keySet()+"     map0  value:"+map.values());
                }
        }
        returnResult.setResult(map);
        returnResult.setMessage("发射弹幕！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 发送弹幕
     * @param request(text,videoId)  ,user.getId
     * @return
     */
    @RequestMapping("/addBarrages")
    @ResponseBody
    @LoginRequired
    public JSONObject addBarrages(@CurrentUser AppUser user, HttpServletRequest request){
        ReturnResult returnResult=new ReturnResult();
        log.info("发送弹幕-------------->>/barrage/addBarrages");
        Map<String, String> mapValue = getParameterMap(request);
        Barrage barrage = new Barrage();
        String videoId=mapValue.get("videoId");
        String text=mapValue.get("text");
        int timePoint=Integer.parseInt(mapValue.get("timePoint"));
        if (StringUtils.isEmpty(videoId)){
            returnResult.setMessage("视频id不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }if (StringUtils.isEmpty(text)){
            returnResult.setMessage("弹幕内容不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }if (timePoint < 0  ){
            returnResult.setMessage("时间点错误！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        barrage.setBarrageId(UUID.randomUUID().toString().toUpperCase());
        barrage.setVideoId(videoId);
        barrage.setText(text);
        barrage.setUserId(user.getId());
        barrage.setTimePoint(timePoint);
        AppUser appUserMsg = loginService.getAppUserMsg("", "", user.getId());
        barrage.setUserName(appUserMsg.getNickName());
        barrage.setUserHeadUrl(appUserMsg.getHeadpUrl());
        barrageService.addBarrage(barrage);

        returnResult.setMessage("添加成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}
