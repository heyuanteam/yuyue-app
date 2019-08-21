package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.BarrageService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("barrage")
public class BarrageController extends BaseController{
    private ReturnResult returnResult=new ReturnResult();
    private Map<String, List<Barrage>> map=new HashMap<>();
    @Autowired
    private BarrageService barrageService;
    @Autowired
    private RedisUtil redisUtil;



    @RequestMapping("getBarrages")
    @ResponseBody
    public JSONObject getBarrages(HttpServletRequest request){
        Map<String, String> mapValue = getParameterMap(request);
        final String videoId = mapValue.get("videoId");
        final String date = mapValue.get("date");
        List<Barrage> list =null;
        if(redisUtil.existsKey(videoId)){
            System.out.println("--------redis-------");
             list = (List<Barrage>)(Object)redisUtil.getList(videoId, 0, -1);
            //数据与传入的时间做业务处理
        }else {
            list = barrageService.getBarrages(videoId);
            redisUtil.setListAll(videoId,list,6000);
        }

        //数据与传入的时间做业务处理
        map.put("barrage",list);
        returnResult.setResult(JSONObject.toJSON(map));
        returnResult.setMessage("发射弹幕！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    @RequestMapping("addBarrages")
    @ResponseBody
    public JSONObject addBarrages(HttpServletRequest request){
        Map<String, String> mapValue = getParameterMap(request);
        Barrage barrage = new Barrage();
        barrage.setId(UUID.randomUUID().toString().toUpperCase());
        barrage.setUserId(mapValue.get("userId"));
        barrage.setText(mapValue.get("text"));
        barrage.setVideoId(mapValue.get("videoId"));
        barrageService.addBarrage(barrage);

        returnResult.setMessage("发送成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult("");
        return ResultJSONUtils.getJSONObjectBean(returnResult);



    }


}
