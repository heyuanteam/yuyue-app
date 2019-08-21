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
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "pay", produces = "application/json; charset=UTF-8")
public class PayController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(PayController.class);

    //微信APPID
    private static final String wxAppId = "wx82e0374be0e044a4";
    //微信商户号
    private static final String wxMchID = "1529278811";
    //微信秘钥
    private static final String KEY = "FE79E95059CDCA91646CDDA6A7F60A93";
    private static final String wxNotifyUrl = "http://101.37.252.177:8082/yuyue-app/pay/wxpayNotify";

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

    /**
     * @Title:wxpayNotify
     * @Description:微信回调
     * @throws Exception
     * @date:2018年7月18日 下午2:32:49
     */
    @ResponseBody
    @RequestMapping(value = "wxpayNotify")
    public void wxpay(HttpServletRequest request, HttpServletResponse response) throws Exception{
        log.info((new StringBuilder()).append("回调的内容为+++++++++++++++++++++++++++++++++").append(request).toString());

        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        for(String line = " "; (line = br.readLine()) != null;)
            buffer.append(line);

        log.info((new StringBuilder()).append("内容++++++++++++").append(buffer.toString()).toString());
        Map object = XMLUtils.xmlString2Map(buffer.toString());
        log.info((new StringBuilder()).append("返回的数据是+++++++").append(object).toString());
        String return_code = object.get("return_code").toString();
//        if(return_code.equals("SUCCESS")) {
//            String orderno = object.get("out_trade_no").toString();
//            log.info((new StringBuilder()).append("\u56DE\u8C03\uFF1A").append(orderno).toString());
//            if(orderno != null && !orderno.equals("")) {
//                PrintUtil print = new PrintUtil(response, "text/html;charset=UTF-8");
//                print.print("SUCCESS");
//            }
//            String respCode = object.get("return_code").toString();
//            String sql = (new StringBuilder()).append("select * from hatchet_order_payment where id='").
//                    append(orderno).append("'").toString();
//            HatchetOrderPayment orderPayment = (HatchetOrderPayment)dao.findFirst(HatchetOrderPayment.class, sql);
//            if(orderPayment != null) {
//                if(respCode.equals("SUCCESS")){
//                    orderPayment.setStatus("10B");
//                }else{
//                    orderPayment.setStatus("70B");
//                }
//                if (orderPayment.getOrderNo().contains("M") && !"M".equals(orderPayment.getPayResCode())) {
//                    String updatesql = "update HATCHET_ORDER_PAYMENT set PAY_RES_CODE ='M' WHERE ID =?";
//                    dao.update(updatesql, new Object[] { orderPayment.getId() });
//                    channelService.upLevelMerchant(orderPayment.getSourceId(), "M", orderPayment.getId(),orderPayment.getUplever());
//                } else if (orderPayment.getOrderNo().contains("T") && !"M".equals(orderPayment.getPayResCode())) {
//                    String updatesql = "update HATCHET_ORDER_PAYMENT set PAY_RES_CODE ='M' WHERE ID =?";
//                    dao.update(updatesql, new Object[] { orderPayment.getId() });
//                    channelService.upLevelMerchant(orderPayment.getSourceId(), "T", orderPayment.getId(),orderPayment.getUplever());
//                }
//                orderPayment.setResponseCode("00");
//                orderPayment.setNote(object.get("result_code").toString());
//                String trxNo = object.get("result_code").toString();
//                orderPayment.setAcqRefNo(respCode);
//                orderPayment.setTerminalRefNo(trxNo);
//                String updatesql = "update HATCHET_ORDER_PAYMENT set create_user_id=?, pay_res_code=?, complete_time=?,status=?,note=?,RESPONSE_CODE=?,ACQ_RESPONSE_CODE=?,acq_ref_no=?,terminal_ref_no=? where id=?";
//                dao.update(updatesql, new Object[] {object.get("out_trade_no"), "10A", new Date(), orderPayment.getStatus(), orderPayment.getNote(), orderPayment.getResponseCode(),
//                        orderPayment.getResponseCode(), orderPayment.getAcqRefNo(), orderPayment.getTerminalRefNo(), orderno});
//                String updateSql = "UPDATE HATCHET_ORDER_PAYMENT SET settle_cycle=0,pay_pre_order_time=? where id=?";
//                dao.update(updateSql, new Object[] { new Date(), orderno
//                });
//            }
//        }
    }

}
