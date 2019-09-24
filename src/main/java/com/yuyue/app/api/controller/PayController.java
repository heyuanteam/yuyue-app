package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.google.common.collect.Maps;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.PayService;
import com.yuyue.app.utils.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
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
import java.net.URL;
import java.util.*;


@RestController
@RequestMapping(value = "/pay", produces = "application/json; charset=UTF-8")
public class PayController {
    private static Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayService payService;
    @Autowired
    private LoginService loginService;

    //微信APPID
    private static final String wxAppId = "wx82e0374be0e044a4";
    private static final String ip = "101.37.252.177";
    //微信AppSecret
    private static final String APP_SECRET = "c08075181dce2ffe3f036734f168318f";
    //微信商户号
    private static final String wxMchID = "1529278811";
    //微信秘钥
    private static final String KEY = "FE79E95059CDCA91646CDDA6A7F60A93";
    private static final String wxNotifyUrl = "http://101.37.252.177:8082/yuyue-app/pay/wxpayNotify";
    // 构造签名的map
    private SortedMap<Object, Object> parameters = new TreeMap<>();
    //扫码回调
    private static final String wxNativeNotify = "http://101.37.252.177:8082/yuyue-app/pay/wxNativeNotify";

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

    //支付宝转账
    private static final String gateway="https://openapi.alipay.com/gateway.do";//支付宝网关
    //填写自己创建的app的对应参数
    private static AlipayClient alipayClient = new DefaultAlipayClient
            (gateway, AliAPPID, AliAppPrivateKey, "json", "utf-8", AliPayPublicKey,"RSA2");

    //苹果内购
    private static final Map<String, Object> iosMap = new HashMap<>();
    static {
        iosMap.put("12",new BigDecimal(8.4));
        iosMap.put("30",new BigDecimal(21));
        iosMap.put("50",new BigDecimal(35));
        iosMap.put("128",new BigDecimal(89.6));
        iosMap.put("618",new BigDecimal(432.6));
        iosMap.put("6,498",new BigDecimal(4548.6));
    }

    @ResponseBody
    @RequestMapping("/payYuYue")
    @LoginRequired
    public JSONObject payYuYue(Order order, @CurrentUser AppUser user) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        log.info("-------创建充值订单-----------");
        if (StringUtils.isEmpty(order.getTradeType())) {
            returnResult.setMessage("充值类型不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        order.setOrderNo("YYCZ" + RandomSaltUtil.randomNumber(14));
        order.setStatus("10A");
        order.setStatusCode("100001");
        order.setMobile(user.getPhone());
        order.setMerchantId(user.getId());
//        order.setTradeType("CZWX");
//        order.setMoney("100");
        createOrder(order);
        if (StringUtils.isEmpty(order.getId())) {
            returnResult.setMessage("创建订单失败！缺少参数！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if ("CZWX".equals(order.getTradeType()) || "GGWX".equals(order.getTradeType())) {
            return payWX(order);
        } else if ("CZZFB".equals(order.getTradeType()) || "GGZFB".equals(order.getTradeType())) {
            return payZFB(order);
        }
        returnResult.setMessage("充值类型选择错误！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    public JSONObject payWX(Order order) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        log.info("-------weixinAPP支付统一下单-----------");
        log.info("订单详情============" + order.toString());
        try {
            Map map = new HashMap();
            log.info("金额==========>>>"+order.getMoney());
            String moneyD = order.getMoney().setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
                    .setScale(0,BigDecimal.ROUND_HALF_UP).toString();
            log.info("金额==========>>>"+moneyD);
            map.put("appid", wxAppId);
            map.put("mch_id", wxMchID);
            map.put("nonce_str", RandomSaltUtil.generetRandomSaltCode(32));
            map.put("body", "yuyue-礼物充值");
            map.put("out_trade_no", order.getId());
            map.put("total_fee", moneyD);
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
            payService.updateStatus(order.getId(), "10C");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * @throws Exception
     * @Title:wxpayNotify
     * @Description:微信回调
     * @date:2018年7月18日 下午2:32:49
     */
    @ResponseBody
    @RequestMapping(value = "/wxpayNotify")
    public JSONObject wxpay(HttpServletRequest request) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        log.info((new StringBuilder()).append("回调的内容为+++++++++++++++++++++++++++++++++").append(request).toString());
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        for (String line = " "; (line = br.readLine()) != null; )
            buffer.append(line);

        log.info((new StringBuilder()).append("内容++++++++++++").append(buffer.toString()).toString());
        Map object = XMLUtils.xmlString2Map(buffer.toString());
        log.info((new StringBuilder()).append("返回的数据是+++++++").append(object).toString());
        String returnCode = object.get("return_code").toString();
        if (returnCode.equals("SUCCESS")) {
            String orderId = object.get("out_trade_no").toString();
            log.info((new StringBuilder()).append("\u56DE\u8C03\uFF1A").append(orderId).toString());
            if (StringUtils.isNotEmpty(orderId)) {
                Order orderNo = payService.getOrderId(orderId);
                if (orderNo != null) {
                    orderNo.setResponseCode(returnCode);
                    orderNo.setResponseMessage(object.get("result_code").toString());
                    orderNo.setStatus("10B");
                    payService.updateOrderStatus(orderNo.getResponseCode(), orderNo.getResponseMessage(), orderNo.getStatus(), orderNo.getOrderNo());
                    payService.updateTotal(orderNo.getMerchantId(), orderNo.getMoney());
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
    public JSONObject payZFB(Order order) throws Exception {
        ReturnResult returnResult = new ReturnResult();
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
            String moneyD = order.getMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
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
                Map map = new HashMap();
                map.put("response",response.getBody());
//                String[] split = response.getBody().split("&");
//                for (int i = 0; i < split.length; i++) {
//                    map.put(split[i].split("=")[0],split[i].split("=")[1]);
//                }
                returnResult.setResult(map);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("支付失败！参数不对！");
            returnResult.setMessage("支付失败！参数不对！");
            payService.updateStatus(order.getId(), "10C");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
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
    @RequestMapping(value = "/alipayNotify")
    public JSONObject alipayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        log.info("支付宝平台回调开始+++++++++++++++++++++++++++++++++");
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
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
        boolean flag = AlipaySignature.rsaCheckV1(params, AliPayPublicKey, "UTF-8", "RSA2");
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
                    payService.updateOrderStatus(orderNo.getResponseCode(), orderNo.getResponseMessage(), orderNo.getStatus(), orderNo.getOrderNo());
                    payService.updateTotal(orderNo.getMerchantId(), orderNo.getMoney());
                    returnResult.setMessage("支付宝回调成功！");
                    returnResult.setStatus(Boolean.TRUE);
                }
            }
            log.info("支付宝平台回调结束+++++++++++++++++++++++++++++++++");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * @param TransactionID        ：交易单号 需要客户端传过来的参数1
     * @param Payload：需要客户端传过来的参数2
     * @throws Exception 苹果内购支付
     * @throws
     * @Title: doIosRequest
     * @Description:Ios客户端内购支付
     */
    @ResponseBody
    @RequestMapping(value = "/doIosRequest")
    @LoginRequired
    public JSONObject doIosRequest(String TransactionID, String Payload, @CurrentUser AppUser user) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        Map<String, Object> map = new HashMap<>();
        System.out.println("客户端传过来的值1：" + TransactionID + "客户端传过来的值2：" + Payload);

        String verifyResult = IosVerifyUtils.buyAppVerify(Payload, 1); //1.先线上测试 发送平台验证
        if (verifyResult == null) { // 苹果服务器没有返回验证结果
            returnResult.setMessage("无订单信息!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else { // 苹果验证有返回结果
            System.out.println("线上，苹果平台返回JSON:" + verifyResult);
            JSONObject job = JSONObject.parseObject(verifyResult);
            String states = job.getString("status");

            if ("21007".equals(states)) {    //是沙盒环境，应沙盒测试，否则执行下面
                verifyResult = IosVerifyUtils.buyAppVerify(Payload, 0);    //2.再沙盒测试 发送平台验证
                System.out.println("沙盒环境，苹果平台返回JSON:" + verifyResult);
                job = JSONObject.parseObject(verifyResult);
                states = job.getString("status");
            }

            System.out.println("苹果平台返回值：job" + job);
            if (states.equals("0")) { // 前端所提供的收据是有效的 验证成功
                String r_receipt = job.getString("receipt");
                JSONObject returnJson = JSONObject.parseObject(r_receipt);
                String in_app = returnJson.getString("in_app");
                JSONObject in_appJson = JSONObject.parseObject(in_app.substring(1, in_app.length() - 1));

                String product_id = in_appJson.getString("product_id");
                String transaction_id = in_appJson.getString("transaction_id"); // 订单号
/************************************************+自己的业务逻辑**********************************************************/
                //如果单号一致 则保存到数据库
                if (TransactionID.equals(transaction_id)) {
                    String[] moneys = product_id.split("\\.");
                    Order order = new Order();
                    order.setOrderNo(TransactionID);
                    order.setStatus("10B");
                    order.setStatusCode("100001");
                    order.setMobile(user.getPhone());
                    order.setMerchantId(user.getId());
                    order.setMoney(new BigDecimal(iosMap.get(moneys[3]).toString()));
                    order.setTradeType("CZIOS");
                    order.setNote(moneys[3]);
//        order.setMoney("100");
                    createOrder(order);
                    payService.updateTotal(user.getId(), new BigDecimal(iosMap.get(moneys[3]).toString()));
                    returnResult.setStatus(Boolean.TRUE);
                    returnResult.setMessage("充值成功！");
                    returnResult.setResult(moneys[3]);
                }
/************************************************+自己的业务逻辑end**********************************************************/
            } else {
                returnResult.setMessage("receipt数据有问题");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    //创建充值订单
    public void createOrder(Order order) {
        order.setId(RandomSaltUtil.generetRandomSaltCode(32));
        payService.createOrder(order);
    }

    //创建提现订单
    public void createOut(OutMoney outMoney) {
        outMoney.setId(RandomSaltUtil.generetRandomSaltCode(32));
        payService.createOut(outMoney);
    }

    //创建收益订单
    public void createShouMoney(ChangeMoney changeMoney) {
        changeMoney.setId(RandomSaltUtil.generetRandomSaltCode(32));
        payService.createShouMoney(changeMoney);
    }

    /**
     * 用户提现
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/outMoney")
    @LoginRequired
    public JSONObject outMoney(OutMoney outMoney, @CurrentUser AppUser user) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        log.info("-------提现订单-----------");
        if (StringUtils.isEmpty(outMoney.getTradeType())) {
            returnResult.setMessage("提现类型不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else if (outMoney.getMoney() == null|| outMoney.getMoney().compareTo(BigDecimal.ZERO)==0){
            returnResult.setMessage("转账的钱不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else if (user.getIncome().compareTo(outMoney.getMoney()) == -1){
            returnResult.setMessage("转账的钱不能高于收益！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else if (StringUtils.isEmpty(user.getOpendId())){
            returnResult.setCode("02");
            returnResult.setMessage("openId为空！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else if (outMoney.getMoney().compareTo(new BigDecimal(50))==-1){
            returnResult.setMessage("转账的钱不能低于50元！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

        outMoney.setOutNo("YYTX" + RandomSaltUtil.randomNumber(14));
        outMoney.setMerchantId(user.getId());
        outMoney.setRealName(user.getWechatName());
        outMoney.setMoneyNumber(user.getOpendId());
//        outMoney.setTradeType("TXZFB");
//        outMoney.setMoney(new BigDecimal("1"));

        createOut(outMoney);
        if (StringUtils.isEmpty(outMoney.getId())) {
            returnResult.setMessage("创建提现订单失败！缺少参数！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if ("TXZFB".equals(outMoney.getTradeType())) {
//            return outZFB(outMoney);
        } else if ("TXWX".equals(outMoney.getTradeType())) {
            return outWX(outMoney,user);
        }
        returnResult.setMessage("提现正在进行中！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    //单笔提现到微信
    private JSONObject outWX(OutMoney outMoney,AppUser user) {
        ReturnResult returnResult = new ReturnResult();
        String nonce_str = RandomSaltUtil.getRandomString(16);
        //是否校验用户姓名 NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名
        String checkName ="NO_CHECK";
        String partner_trade_no = RandomSaltUtil.generetRandomSaltCode(32);
        //描述
        log.info("金额==========>>>"+outMoney.getMoney());
        String moneyD = outMoney.getMoney()
                .setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
                .setScale(0,BigDecimal.ROUND_HALF_UP).toString();
        log.info("金额==========>>>"+moneyD);
        String desc = "娱悦APP提现"+outMoney.getMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString()+"元";
        // 参数：开始生成第一次签名
        parameters.put("mch_appid", wxAppId);
        parameters.put("mchid", wxMchID);
        parameters.put("partner_trade_no", partner_trade_no);
        parameters.put("nonce_str", nonce_str);
        parameters.put("openid", user.getOpendId());
        parameters.put("check_name", checkName);
        parameters.put("amount", moneyD);
        parameters.put("spbill_create_ip", ip);
        parameters.put("desc", desc);
        String sign = XMLUtils.createSign("UTF-8", parameters);
        log.info("sign==========>>>>"+sign);
        Map map = new HashMap();
        map.put("amount",moneyD);
        map.put("check_name",checkName);
        map.put("desc",desc);
        map.put("mch_appid",wxAppId);
        map.put("mchid",wxMchID);
        map.put("nonce_str",nonce_str);
        map.put("openid",user.getOpendId());
        map.put("partner_trade_no",partner_trade_no);
        map.put("sign",sign);
        map.put("spbill_create_ip",ip);
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        XMLUtils.mapToXMLTest2(map, sb);
        sb.append("</xml>");
        log.info((new StringBuilder()).append("上送的数据为+++++++").append(sb.toString()).toString());
        try {
            CloseableHttpResponse response = HttpUtils.Post(
                    "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", sb.toString(), true);
            String transfersXml = EntityUtils.toString(response.getEntity(), "utf-8");
            Map<String, String> transferMap = XMLUtils.xmlString2Map(transfersXml);
            log.info("微信转账回返信息=============>>>>>>"+transferMap.toString());
            if (transferMap.size()>0) {
                if (transferMap.get("result_code").equals("SUCCESS") && transferMap.get("return_code").equals("SUCCESS")) {
                    //成功需要进行的逻辑操作，
                    returnResult.setMessage("企业转账成功");
                    returnResult.setStatus(Boolean.TRUE);
                    payService.updateOutStatus(transferMap.get("result_code"), "微信转账成功", "10B", outMoney.getOutNo());
                    payService.updateOutIncome(user.getId(),outMoney.getMoney());
                } else {
                    //失败原因
                    returnResult.setMessage("企业转账失败");
                    payService.updateOutStatus(transferMap.get("err_code_des"), transferMap.get("return_msg"), "10C", outMoney.getOutNo());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyExceptionUtils.mxe("企业付款异常" + e.getMessage());
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    //opendId保存到个人信息里面
    @ResponseBody
    @RequestMapping("/saveUserInfo")
    @LoginRequired
    public JSONObject saveUserInfo(@CurrentUser AppUser user,String tradeType,String code) {
        ReturnResult returnResult = new ReturnResult();
        if (StringUtils.isEmpty(tradeType)) {
            returnResult.setMessage("提现类型不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else if (StringUtils.isEmpty(code)){
            returnResult.setMessage("code不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

        if("TXZFB".equals(tradeType)){

        } else if ("TXWX".equals(tradeType)) {
            String opendId = "";
            String wechatName = "";
            JSONObject userInfo = new JSONObject();
            try {
//          获取个人信息
                userInfo = getUserInfo(getOpenId(code));
                opendId = userInfo.getString("openid");
                wechatName = userInfo.getString("nickname");
                loginService.updateAppUser(user.getId(),"","","","","","", "",
                        "", "","","","","","","","",
                        "",opendId,wechatName,"");
                returnResult.setMessage("获取openid成功！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            } catch (Exception e) {
                log.info("获取openid失败: ===>>>"+e.getMessage());
                returnResult.setMessage("获取openid失败！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        returnResult.setMessage("保存类型选择错误！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    //删除opendId
    @ResponseBody
    @RequestMapping("/deleteOpendId")
    @LoginRequired
    public JSONObject deleteOpendId(@CurrentUser AppUser user,String tradeType) {
        ReturnResult returnResult = new ReturnResult();
        if (StringUtils.isEmpty(tradeType)){
            returnResult.setMessage("类型不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

        if("ZFB".equals(tradeType)){

        } else if ("WX".equals(tradeType)) {
            String opendId = " ";
            String wechatName = " ";
            try {
                loginService.updateOpendId(user.getId(),opendId,wechatName);
                returnResult.setMessage("解绑成功！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            } catch (Exception e) {
                log.info("解绑失败: ===>>>"+e.getMessage());
                returnResult.setMessage("解绑失败！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        returnResult.setMessage("解绑类型错误！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取用户基本信息
     * @return
     */
    private JSONObject getUserInfo(JSONObject jsonObject){
        String accessToken = jsonObject.getString("access_token");
        String openid = jsonObject.getString("openid");
//         GET
//        https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
//        https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        String url = "https://api.weixin.qq.com/sns/userinfo?"
                + "access_token="+ accessToken
                + "&openid="+ openid
                + "&lang=zh_CN";
        String result = getReturnData(url,"UTF-8");
        JSONObject json = JSON.parseObject(result);
        log.info("获取用户基本信息======>>>>>"+json);
        return json;
    }

    /**
     * 获取用户openID
     * @Author  yuhao
     * @param code
     * @return  String
     * @Date	2018年9月3日
     */
    public JSONObject getOpenId(String code){
        log.info("code: ===>>>"+code);
        JSONObject jsonObject = new JSONObject();
        if (code != null) {
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                    + "appid="+ wxAppId
                    + "&secret="+ APP_SECRET
                    + "&code=" + code + "&grant_type=authorization_code";
            String returnData = getReturnData(url,"UTF-8");
            log.info("用户授权====>>>>"+returnData);
            try {
                jsonObject = JSONObject.parseObject(returnData);
            } catch (Exception e) {
                jsonObject = JSONObject.parseObject(returnData);
                String errcode = jsonObject.getString("errcode");
                log.info("获取openid失败: ===>>>"+errcode);
            }
            log.info("openid======>>>>>"+jsonObject.getString("openid"));
        }
        return jsonObject;
    }

    public String getReturnData(String urlString,String enCode) {
        String res = "";
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.connect();
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), enCode));
            String line;
            while ((line = in.readLine()) != null) {
                res += line;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 单笔提现到支付宝账户
     * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.54Ty29&treeId=193&articleId=106236&docType=1
     */
    public JSONObject outZFB(OutMoney outMoney) {
        ReturnResult returnResult = new ReturnResult();
        AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
        model.setOutBizNo(outMoney.getId());//生成订单号
        model.setPayeeType("ALIPAY_LOGONID");//固定值
        model.setPayeeAccount(outMoney.getMoneyNumber());
        model.setAmount(outMoney.getMoney().toString());//金额
        model.setPayerShowName("艺人收益");
        model.setPayerRealName(outMoney.getRealName());
        model.setRemark("单笔转账到支付宝");
        try {
            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
            request.setBizModel(model);
            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
            log.info("转账信息=======>"+response.getBody());
            if (response.isSuccess()) {
                JSONObject jsonObject = JSONObject.parseObject(response.getBody()).getJSONObject("alipay_fund_trans_toaccount_transfer_response");
                String msg = jsonObject.getString("msg");
                String code = jsonObject.getString("code");
                String outNo = jsonObject.getString("out_biz_no");
                payService.updateOutStatus(code, msg, "10B", outNo);
                payService.updateOutIncome(outMoney.getMerchantId(), outMoney.getMoney());
                returnResult.setMessage("支付宝转账成功！");
                returnResult.setStatus(Boolean.TRUE);
                returnResult.setResult(JSONObject.parseObject(response.getBody()));
            } else {
                returnResult.setResult(response.getBody());
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("转账失败异常=======>"+e.getMessage());
            returnResult.setMessage("支付宝转账失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 扫码支付
     * @param order
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/payNative")
    @LoginRequired
    public JSONObject payNative(Order order, @CurrentUser AppUser user) throws Exception {
        ReturnResult returnResult = new ReturnResult();
        log.info("-------创建扫码订单-----------");
        if (StringUtils.isEmpty(order.getTradeType())) {
            returnResult.setMessage("充值类型不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        order.setOrderNo("YYSM" + RandomSaltUtil.randomNumber(14));
        order.setStatus("10A");
        order.setMobile(user.getPhone());
        order.setMerchantId(user.getId());
//        order.setTradeType("SMWX");
//        order.setMoney("100");
        createOrder(order);
        if (StringUtils.isEmpty(order.getId())) {
            returnResult.setMessage("创建订单失败！缺少参数！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if ("SMWX".equals(order.getTradeType())) {
            return payNativeWX(order);
        } else if ("SMZFB".equals(order.getTradeType())) {
            return payNativeZFB(order);
        }
        returnResult.setMessage("充值类型选择错误！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    private JSONObject payNativeZFB(Order order) {
        ReturnResult returnResult = new ReturnResult();
        returnResult.setMessage("充值类型选择错误！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    private JSONObject payNativeWX(Order order) {
        ReturnResult returnResult = new ReturnResult();
        HashMap<String, String> paramMap = Maps.newHashMap();
        paramMap.put("trade_type", "NATIVE"); //交易类型
        paramMap.put("spbill_create_ip",ResultJSONUtils.localIp()); //本机的Ip
//        paramMap.put("product_id", payOrderIdsStr); // 商户根据自己业务传递的参数 必填
//        paramMap.put("body", "扫码充值");         //描述
//        paramMap.put("out_trade_no", payOrderIdsStr); //商户 后台的贸易单号
        paramMap.put("total_fee", ""); //金额必须为整数  单位为分
        paramMap.put("notify_url", wxNativeNotify); //支付成功后，回调地址
        paramMap.put("appid", wxAppId); //appid
        paramMap.put("mch_id", wxMchID); //商户号
        paramMap.put("nonce_str", RandomSaltUtil.generetRandomSaltCode(32));  //随机数
        String sign = MD5Utils.signDatashwx(paramMap, KEY);
        paramMap.put("sign",sign);//根据微信签名规则，生成签名
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        XMLUtils.mapToXMLTest2(paramMap, sb);
        sb.append("</xml>");
        String xmlData = sb.toString();

        returnResult.setMessage("充值类型选择错误！！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
}
