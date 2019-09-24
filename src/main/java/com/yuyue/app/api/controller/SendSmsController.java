package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.JPush;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.SendSmsService;
import com.yuyue.app.config.JPushClients;
import com.yuyue.app.utils.RandomSaltUtil;
import com.yuyue.app.utils.RedisUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.SmsUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/send", produces = "application/json; charset=UTF-8")
//@Component
public class SendSmsController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(SendSmsController.class);

    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JPushClients jPushClients;
    @Autowired
    private SendSmsService sendSmsService;

    //极光推送
    @Value("${jpush.appKey}")
    private String appKey;

    @Value("${jpush.masterSecret}")
    private String masterSecret;

    @Value("${jpush.apnsProduction}")
    private boolean apnsProduction;

    private static final String signName = "娱悦APP";
    private static final String templateCode = "SMS_172100731";

    /**
     * 发送消息验证码，通用接口
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public JSONObject sendSms(HttpServletRequest request, HttpServletResponse response){
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        log.info("发送消息验证码，通用接口-------------->>/send/sendSms");
        Map<String, String> map = getParameterMap(request);
        HashMap<String,String> hashMap = Maps.newHashMap();
        ReturnResult result =new ReturnResult();
        String lcode = RandomSaltUtil.randomNumber(6);
        try {
            map.put("template_code", templateCode);
            map.put("sign_name", signName);
            hashMap.put("code",lcode);
            map.put("param", JSON.toJSON(hashMap).toString());

            log.info("验证码：=========="+lcode);

//            SendSmsResponse response = smsUtil.sendSms(
//                    map.get("mobile"), map.get("template_code") , map.get("sign_name")  , map.get("param") );
//
//            System.out.println("短信接口返回的数据----------------");
//            System.out.println("Code=" + response.getCode());
//            System.out.println("Message=" + response.getMessage());
//            System.out.println("RequestId=" + response.getRequestId());
//            System.out.println("BizId=" + response.getBizId());

//            if("OK".equals(response.getCode()) ){
            if("OK".equals("OK") ){
                redisUtil.setString(map.get("mobile"),lcode);
                redisUtil.expire(map.get("mobile"),5L*60L);

                result.setMessage("短信发送成功!");
                result.setStatus(Boolean.TRUE);
                result.setResult(lcode);
            } else {
                result.setMessage("短信发送失败！");
                log.info("短信发送失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("短信发送失败！");
            log.info("短信发送失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 极光推送
     */
    @RequestMapping("/sendJPush")
    @ResponseBody
//    @Async // 异步方法
    public JSONObject sendJPush(HttpServletRequest request) {
        log.info("极光推送-------------->>/sendJPush/sendJPush");
        getParameterMap(request);
//        用户ID,别名
//        List<String> aliasList = Arrays.asList("239");
//        String notificationTitle = "通知内容标题";
//        String msgTitle = "消息内容标题";
//        String msgContent = "消息内容";
        List<JPush> list = sendSmsService.getValid();
        ReturnResult result =new ReturnResult();
        if(CollectionUtils.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                JPush jPush = list.get(i);
                if(jPush != null){
                    try {
                        jPushClients.sendToAll(jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), jPush.getExtras(),
                                apnsProduction,masterSecret,appKey);
                        sendSmsService.updateValid("10B",jPush.getId());
                        result.setMessage("极光推送成功!");
                        result.setResult(JSONArray.parseArray(JSON.toJSONString(list)));
                        result.setStatus(Boolean.TRUE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("极光推送失败！");
                        result.setMessage("极光推送失败！");
                        sendSmsService.updateValid("10C",jPush.getId());
                    }
                }
            }
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }
}
