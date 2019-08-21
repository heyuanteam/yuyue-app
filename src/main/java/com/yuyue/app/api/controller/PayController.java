package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.utils.MD5Utils;
import com.yuyue.app.utils.RandomSaltUtil;
import com.yuyue.app.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "pay", produces = "application/json; charset=UTF-8")
public class PayController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(PayController.class);

    private static final String wxAppId = "娱悦APP";
    private static final String wxMchID = "娱悦APP";
    private static final String KEY = "娱悦APP";
    private static final String wxNotifyUrl = "娱悦APP";

    @ResponseBody
    @RequestMapping("payWX")
    public String payWX(HttpServletRequest request)throws Exception {
        log.info("-------weixinAPP支付统一下单-----------");
        Map<String, String> hashMap = getParameterMap(request);
        Map map = new HashMap();
        map.put("appid", wxAppId);
        map.put("mch_id", wxMchID);
        map.put("nonce_str", RandomSaltUtil.generetRandomSaltCode(32));
        map.put("body", "yuyue-礼物充值");
        map.put("out_trade_no", hashMap.get("orderId"));
        map.put("total_fee", hashMap.get("money"));
        map.put("spbill_create_ip", "101.37.252.177");
        map.put("trade_type", "APP");
        map.put("notify_url", wxNotifyUrl);
        String sign = MD5Utils.signDatashwx(map, KEY);
        map.put("sign", sign);
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        XMLUtils.mapToXMLTest2(map, sb);
        sb.append("</xml>");
        log.info((new StringBuilder()).append("上送的数据为+++++++").append(sb.toString()).toString());
        String res = XMLUtils.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder", sb.toString(), "UTF-8", "application/json");
        log.info("返回的数据为--------------------------+++++++" + res);
        Map ValidCard = XMLUtils.xmlString2Map(res);
        Map maps = new HashMap();
        String timestamp = String.valueOf((new Date()).getTime() / 1000L);
        maps.put("appid", ValidCard.get("appid").toString());
        maps.put("partnerid", wxMchID);
        maps.put("prepayid", ValidCard.get("prepay_id"));
        maps.put("package", "Sign=WXPay");
        maps.put("noncestr", ValidCard.get("nonce_str"));
        maps.put("timestamp", timestamp);
        //maps.put("signType", "MD5");
        String signs = MD5Utils.signDatashwx(maps, KEY);
        maps.put("sign", signs);
        return JSONObject.toJSONString(maps);
    }



}
