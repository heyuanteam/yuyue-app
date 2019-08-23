package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.yuyue.app.api.domain.Order;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.PayService;
import com.yuyue.app.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@RestController
@RequestMapping(value = "pay", produces = "application/json; charset=UTF-8")
public class PayController{
    private static Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayService payService;

    //微信APPID
    private static final String wxAppId = "wx82e0374be0e044a4";
    //微信商户号
    private static final String wxMchID = "1529278811";
    //微信秘钥
    private static final String KEY = "FE79E95059CDCA91646CDDA6A7F60A93";
    private static final String wxNotifyUrl = "http://101.37.252.177:8082/yuyue-app/pay/wxpayNotify";

    //支付宝
    private static final String AliAPPID = "2019082166401163";
    private static final String AliAppPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDD5vbz+U6bkpwHTnto4fg2" +
            "er1oyxMHnGOPGeEl0MA2xuLtcTw8nPqp+ZMLGRWZxr7YLXv/372G6017ruKUE9Wh3ZrxestCDxDnhi7hfeLFPcsIryhpBTchfd7NhPJngGSUS8S" +
            "O4W+x5Yco40fITdnTrGz+fFCnRuUOU7dzo4tHCkrkJlX4cZ6D1PR9IxRCvlCFbxYzTBsqVpn5Ekc+B3RcxycX+yP7CdFSn7frp+uJBHfwDz//NK" +
            "X7OJbJDflNCacU4AFzF8M6wqs7bwIhi0GCZyZeS7FeCFuSAp7MW7Mk6OFDW/OM5bKftu+hwRxQ08o5ynh/6LapItEQSK4JxVGdAgMBAAECggEBA" +
            "LV59VWHozldNGtUWeCMTKrzQxmb3fIT/uqm17p3Ski0L640Us/3wAHL8Fq8jxUYVtzeLduYQfOFcQ7dsInqYeID7zA6R6bXXBqOZEmBm5yKpNZT" +
            "pMS9DxhYiRisSv50ozf5hImz7wvGjFHlUi8NZ3e+aG3Lbc+4TiLajLx0SWaxVEtBeHDzy3MYt4wf0soxHW040rOhk07YMf8g8W6yh7VX6OkkzVs" +
            "UwtKk1iaUKE4xDuDvLOi1f3RwHRC+c4glfcNQ8EkGYupP760HmsdTC//Cl4XMfzQOoeMSH46xVjmWD+tBJ7zooQW6bm8mWCYYc4t/RNw/5nrkXx" +
            "qd73qKEQECgYEA4HoDIncDXRD3gC6idYNEwDy8iC5UolPejHosQpYgG86XRp0NJYQ+JU9kQFj3YNAH2ks+yqzQq6LRaaci6ggpKGyvyBNk7FoFm" +
            "sUCdBi6+oMXq4M4PwfuZ5IygBh1rLM8tQ+qOYI9Lj/2i46+5b2DB3RBWo1FtnQ7LA5Pl55Txl0CgYEA32myHFDxHoScyMjfqSALzOKMAYrZlIGH" +
            "pZC7Lr6H2T/lrcQHAEpVkAZSAqrhKB0W9QwtkWBDAtV0jK75GIsTkVrJtIf2BSLnQ9wuI9qiDoYckyEw7xoV9uBV1Cxg5VoJZ+0I4su2zKE/YnG" +
            "0OEkbg40GMgaJTRlahBEgrYGYhEECgYB0JQ08JuH5pE665uYt8TaAVKyjtX0a5FQw0QHXjf+dA55n7diggbT57wMK/D06vUhi3S3nBdWOCNdbWB" +
            "wLhR9uiBXHaql8VPOzaZ3kXetYtL1pg6J1km/67LzuZDl2muKdODa2PLnVFUlGWhxRmGWUVMV/ybq5NZhsKhdqdoQYDQKBgG2dc1k3UYaStEZDY" +
            "JGfeoq1INJk6OpXP1G5mE2QCCFMm4lNY839qst2fmh2pPBEjY3/wp/QZjCOwJeCBg/HtPsdW1frWYcdn/CIqE7JJ7gOjxiVMWgvGVW+rf3jJEuD" +
            "iJfoEfMM1ozCFNJdTXpMTGaYG9ERqe4dIW8o5CqdKlLBAoGAbx4zaDeq4NFHmLYxOwUoDqOOaFEAOOR2ff1fBk/xoEpR/E2piI87sQs5l+V5L9w" +
            "/TyV/61yQ6rbG/Pvlzzjj1n7/PbLmdSBWBYQjqK8TO3vOcCqsb6duJrcHPxLC48ieZ1ikIZEtRSEz4GrV62qqFNmBe09FrTjaUjo9Dn+NFDo=";
    private static final String AliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArkUo049kqx1Y6mme9Y1pjxNesSZgT" +
            "LLcwdxRDS3JtkmhWgO/SX2xIFqkbmkMspm94iXklqwG5msWL23I5WTXjLHNGdv5mU9cKx64gN9atOsA0sQ38yiInYMd6PPBM4VOdvKyau0purE9" +
            "RQqwKd1O/XTFlD9XDxEz3NiRD6sunLIxaPMMkt2+X7KPXAwYBIL5tymna3+rBnxYIAX2q5KORaYKoOWRK9ER+pMMXpcqNbMdO1ceOeUqx2XzpVZ" +
            "oMlcgRB6BTKG59S+KVso1O9Cxx52lvYqisuei8OnNwmMxK+++psZXmdDuNpUc4OJXdA7Bc0zbwDedtxRJE3zNDONOOwIDAQAB";
    private static final String AliPayNotifyUrl = "http://101.37.252.177:8082/yuyue-app/pay/alipayNotify";

    private ReturnResult returnResult =new ReturnResult();

    @ResponseBody
    @RequestMapping("payYuYue")
    public JSONObject payYuYue(Order order)throws Exception {
        log.info("-------创建订单-----------");
//        order.setTradeType("CZWX");
//        order.setMoney("100");
//        order.setModle("13576034501");
//        order.setMerchantId("B044C53B38BA4E84B507E62402683E26");
        createOrder(order);
        if (StringUtils.isEmpty(order.getId())){
            returnResult.setMessage("创建订单失败！缺少参数！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if("CZWX".equals(order.getTradeType())){
            return payWX(order);
        } else if("CZZFB".equals(order.getTradeType())){
            return payZFB(order);
        }
        return null;
    }

    public JSONObject payWX(Order order)throws Exception {
        log.info("-------weixinAPP支付统一下单-----------");
        try {
            Map map = new HashMap();
            map.put("appid", wxAppId);
            map.put("mch_id", wxMchID);
            map.put("nonce_str", RandomSaltUtil.generetRandomSaltCode(32));
            map.put("body", "yuyue-礼物充值");
            map.put("out_trade_no", order.getId());
            map.put("total_fee", order.getMoney());
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
    //        return JSONObject.toJSONString(maps);
            returnResult.setMessage("返回成功！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(JSONObject.toJSON(maps));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("支付失败！参数不对！");
            returnResult.setMessage("支付失败！参数不对！");
            payService.updateStatus(order.getId(),"10C");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * @Title:wxpayNotify
     * @Description:微信回调
     * @throws Exception
     * @date:2018年7月18日 下午2:32:49
     */
    @ResponseBody
    @RequestMapping(value = "wxpayNotify")
    public JSONObject wxpay(HttpServletRequest request) throws Exception{
        log.info((new StringBuilder()).append("回调的内容为+++++++++++++++++++++++++++++++++").append(request).toString());
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        for(String line = " "; (line = br.readLine()) != null;)
            buffer.append(line);

        log.info((new StringBuilder()).append("内容++++++++++++").append(buffer.toString()).toString());
        Map object = XMLUtils.xmlString2Map(buffer.toString());
        log.info((new StringBuilder()).append("返回的数据是+++++++").append(object).toString());
        String returnCode = object.get("return_code").toString();
        if(returnCode.equals("SUCCESS")) {
            String orderId = object.get("out_trade_no").toString();
            log.info((new StringBuilder()).append("\u56DE\u8C03\uFF1A").append(orderId).toString());
            if(StringUtils.isNotEmpty(orderId)) {
                Order orderNo = payService.getOrderId(orderId);
                if(orderNo != null) {
                    orderNo.setResponseCode(returnCode);
                    orderNo.setResponseMessage(object.get("result_code").toString());
                    orderNo.setStatus("10B");
                    payService.updateOrderStatus(orderNo.getResponseCode(),orderNo.getResponseMessage(),orderNo.getStatus(),orderNo.getOrderNo());
                    returnResult.setMessage("微信回调成功！");
                    returnResult.setStatus(Boolean.TRUE);
                }
            }
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 支付宝APP支付统一下单接口
     *
     * @return
     */
    public JSONObject payZFB(Order order)throws Exception {
        log.info("======支付宝APP支付统一下单接口==============");
        try {
            // 实例化客户端
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                    AliAPPID, AliAppPrivateKey, "json", "UTF-8", AliPayPublicKey, "RSA2");
            // 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            // SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setBody("充值消费");
            model.setSubject("充值消费");
            model.setOutTradeNo(order.getId());
            model.setTimeoutExpress("30m");

            // 将分制金额换成元制金额保留两位小数
            String moneyD = new BigDecimal(order.getMoney()).divide(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            model.setTotalAmount(moneyD);
            model.setProductCode("QUICK_MSECURITY_PAY");// 固定值
            request.setBizModel(model);
            request.setNotifyUrl(AliPayNotifyUrl);// 商户外网可以访问的异步地址
            try {
                // 这里和普通的接口调用不同，使用的是sdkExecute
                AlipayTradeAppPayResponse response = alipayClient
                        .sdkExecute(request);
                log.info("response: " + response.getBody());// 就是orderString
                // 可以直接给客户端请求，无需再做处理。
                returnResult.setMessage("返回成功！");
                returnResult.setStatus(Boolean.TRUE);
                returnResult.setResult(response.getBody());
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("支付失败！参数不对！");
            returnResult.setMessage("支付失败！参数不对！");
            payService.updateStatus(order.getId(),"10C");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 支付宝回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "alipayNotify")
    public JSONObject alipayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("支付宝平台回调开始+++++++++++++++++++++++++++++++++");
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        log.info("params: " + params);
        // 切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        // boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String
        // publicKey, String charset, String sign_type)
        String orderId = params.get("out_trade_no");
        boolean flag = AlipaySignature.rsaCheckV1(params,AliPayPublicKey, "UTF-8", "RSA2");
        if (flag) {
            log.info("支付宝验签成功+++++++++++++++++++++++++++++++++");
            Order orderNo = payService.getOrderId(orderId);
            if (orderNo != null) {
                // 有可能出现多次回调，只有在该状态下的回调才是支付成功下的回调
                if (params.get("trade_status").equals("TRADE_SUCCESS") || params.get("trade_status").equals("TRADE_FINISHED")) {
                    String trxNo = params.get("trade_status");
                    //加钱
                    orderNo.setResponseCode(trxNo);
                    orderNo.setResponseMessage(trxNo);
                    orderNo.setStatus("10B");
                    payService.updateOrderStatus(orderNo.getResponseCode(),orderNo.getResponseMessage(),orderNo.getStatus(),orderNo.getOrderNo());
                    returnResult.setMessage("支付宝回调成功！");
                    returnResult.setStatus(Boolean.TRUE);
                }
            }
            log.info("支付宝平台回调结束+++++++++++++++++++++++++++++++++");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * @throws Exception
     * 苹果内购支付
     * @Title: doIosRequest
     * @Description:Ios客户端内购支付
     * @param TransactionID ：交易单号 需要客户端传过来的参数1
     * @param Payload：需要客户端传过来的参数2
     * @throws
     */
    public Map<String, Object> doIosRequest(String TransactionID,String Payload, int userId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> mapChange = new HashMap<>();
        System.out.println("客户端传过来的值1："+TransactionID+"客户端传过来的值2："+Payload);

        String verifyResult = IosVerifyUtils.buyAppVerify(Payload,1); //1.先线上测试 发送平台验证
        if (verifyResult == null) { // 苹果服务器没有返回验证结果
            System.out.println("无订单信息!");
        } else { // 苹果验证有返回结果
            System.out.println("线上，苹果平台返回JSON:"+verifyResult);
            JSONObject job = JSONObject.parseObject(verifyResult);
            String states = job.getString("status");

            if("21007".equals(states)){	//是沙盒环境，应沙盒测试，否则执行下面
                verifyResult = IosVerifyUtils.buyAppVerify(Payload,0);	//2.再沙盒测试 发送平台验证
                System.out.println("沙盒环境，苹果平台返回JSON:"+verifyResult);
                job = JSONObject.parseObject(verifyResult);
                states = job.getString("status");
            }

            System.out.println("苹果平台返回值：job"+job);
            if (states.equals("0")){ // 前端所提供的收据是有效的 验证成功
                String r_receipt = job.getString("receipt");
                JSONObject returnJson = JSONObject.parseObject(r_receipt);
                String in_app = returnJson.getString("in_app");
                JSONObject in_appJson = JSONObject.parseObject(in_app.substring(1, in_app.length()-1));

                String product_id = in_appJson.getString("product_id");
                String transaction_id = in_appJson.getString("transaction_id"); // 订单号
/************************************************+自己的业务逻辑**********************************************************/
                //如果单号一致 则保存到数据库
                if(TransactionID.equals(transaction_id)){
                    String [] moneys = product_id.split("\\.");
//                    System.out.println("用户ID："+userId+",要充值的钻石数："+moneys[moneys.length-1]);
//                    mapChange = charge(Integer.parseInt(moneys[moneys.length-1]), 5, userId);
                    map.put("money", moneys[moneys.length-1]);
                }
/************************************************+自己的业务逻辑end**********************************************************/
                if((boolean) mapChange.get("success")){//用户钻石数量新增成功
                    map.put("success", true);
                    map.put("message", "充值钻石成功！");
                }else{
                    map.put("success", false);
                    map.put("message", "充值钻石失败！");
                }
            } else {
                map.put("success", false);
                map.put("message", "receipt数据有问题");
                map.put("status", states);
            }
        }
        return map;
    }

    //创建订单
    public void createOrder(Order Order){
        Order.setId(RandomSaltUtil.generetRandomSaltCode(32));
        Order.setOrderNo("YYCZ"+RandomSaltUtil.randomNumber(14));
        Order.setStatus("10A");
        Order.setStatusCode("100001");
        payService.createOrder(Order);
    }

}
