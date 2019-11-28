package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.Item;
import com.yuyue.app.api.service.ItemRepository;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/send", produces = "application/json; charset=UTF-8")
public class SendSmsController extends BaseController{

    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemRepository itemRepository;

    private static final String signName = "娱悦APP";
    private static final String templateCode = "SMS_172100731";

    /**
     * 发送消息验证码，通用接口
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public JSONObject sendSms(HttpServletRequest request, HttpServletResponse response){
        log.info("发送消息验证码，通用接口-------------->>/send/sendSms");
        Map<String, String> map = getParameterMap(request, response);
        HashMap<String,String> hashMap = Maps.newHashMap();
        ReturnResult result =new ReturnResult();
        String lcode = RandomSaltUtil.randomNumber(6);
        try {
            if (StringUtils.isEmpty(map.get("mobile"))) {
                result.setMessage("缺少手机号！");
                return ResultJSONUtils.getJSONObjectBean(result);
            }
            map.put("template_code", templateCode);
            map.put("sign_name", signName);
            hashMap.put("code",lcode);
            map.put("param", JSON.toJSON(hashMap).toString());
            log.info("验证码：=========="+lcode);
            SendSmsResponse sendSmsResponse = smsUtil.sendSms(
                    map.get("mobile"), map.get("template_code") , map.get("sign_name")  , map.get("param") );
//
//            System.out.println("短信接口返回的数据----------------");
//            System.out.println("Code=" + response.getCode());
//            System.out.println("Message=" + response.getMessage());
//            System.out.println("RequestId=" + response.getRequestId());
//            System.out.println("BizId=" + response.getBizId());

            if("OK".equals(sendSmsResponse.getCode()) ){
//            if("OK".equals("OK") ){
                redisUtil.setString(map.get("mobile"),lcode);
                redisUtil.expire(map.get("mobile"),5L*60L);
                result.setMessage("短信发送成功!");
                result.setStatus(Boolean.TRUE);
//                result.setResult(lcode);
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
     * Elasticsearch索引测试
     */
    @RequestMapping("/toElasticsearch")
    @ResponseBody
    public JSONObject toElasticsearch(HttpServletRequest request, HttpServletResponse response){
        log.info("Elasticsearch索引测试-------------->>/send/toElasticsearch");
        getParameterMap(request, response);
        ReturnResult result =new ReturnResult();
//        创建Elasticsearch索引，会根据Item类的@Document注解信息来创建
//        elasticsearchTemplate.createIndex(Item.class);//创建Elasticsearch索引
//        elasticsearchTemplate.deleteIndex(Item.class);//删除Elasticsearch索引
//        新增一个对象
        Item item = new Item(1L, "小米手机7", " 手机", "小米", 3499.00, "http://image.baidu.com/13123.jpg");
//        itemRepository.save(item);
//        批量新增
        List<Item> list = new ArrayList<>();
        list.add(item);
        list.add(new Item(2L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", " 手机", "华为", 4499.00, "http://image.baidu.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.save(list);
        //删除Elasticsearch索引
//        elasticsearchTemplate.deleteIndex(Item.class);

        Item one = itemRepository.findOne(2L);
        result.setResult(one);
        result.setStatus(Boolean.TRUE);
        return  ResultJSONUtils.getJSONObjectBean(result);
    }
}
