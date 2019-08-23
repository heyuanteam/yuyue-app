package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.BarrageService;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value = "barrage" ,produces = "application/json; charset=UTF-8")
public class BarrageController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(BarrageController.class);

    @Autowired
    private BarrageService barrageService;
    @Autowired
    private RedisUtil redisUtil;

    private ReturnResult returnResult=new ReturnResult();
    private Map<String, List<Barrage>> map=new HashMap<>();

    /**
     * 获取弹幕信息
     * @param request
     * @return
     */
    @RequestMapping("getBarrages")
    @ResponseBody
    public JSONObject getBarrages(HttpServletRequest request){
        Map<String, String> mapValue = getParameterMap(request);
        final String videoId = mapValue.get("videoId");
        final String date = mapValue.get("date");
        List<Barrage> list =null;
        if(redisUtil.existsKey(videoId)){
            /* list = (List<Barrage>)(Object)redisUtil.getList(videoId, 0, -1);*/
            list=JSON.parseObject((String) redisUtil.getString("barrage" + videoId),
                    new TypeReference<List<Barrage>>() {});
            for (Barrage b: list) {
                System.out.println("--------redis-------"+b);
            }
            //数据与传入的时间做业务处理
        }else {
            list = barrageService.getBarrages(videoId);
            redisUtil.setString("barrage"+videoId, JSON.toJSONString(list),6000);
            /*redisUtil.setListAll(videoId,list,6000);*/
        }
        //数据与传入的时间做业务处理
        map.put("barrage",list);
        returnResult.setResult(JSONObject.toJSON(map));
        returnResult.setMessage("发射弹幕！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 发送弹幕
     * @param request
     * @return
     */
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

        returnResult.setMessage("添加成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}
