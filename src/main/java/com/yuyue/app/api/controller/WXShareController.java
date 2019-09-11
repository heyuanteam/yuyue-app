package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.WXShare;
import com.yuyue.app.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/share" ,method = RequestMethod.POST,produces = "application/json; charset=UTF-8")
public class WXShareController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(WXShareController.class);
    @Autowired
    private RedisUtil redisUtil;




    /**
     * 获取微信分享 signature，nonceStr，timestamp 和appId
     * @param request
     * @return
     */
    @RequestMapping("/test")
    @ResponseBody
    public JSONObject test(HttpServletRequest request){
        //http://101.37.252.177:8082/yuyue-app/share/WXShare
        //IP   101.37.252.177
        request.getServerName();
        System.out.println("ServerName:"+request.getServerName());
        //端口   8082
        request.getServerPort();
        System.out.println("ServerPort:"+request.getServerPort());
        //项目名  yuyue-app
        request.getContextPath();
        System.out.println("ContextPath:" + request.getContextPath());
        //servlet路径，方法接口路径  /share/WXShare
        request.getServletPath();
        System.out.println("ServletPath:" + request.getServletPath());
        //uri:请求的路径  /share/WXShare
        request.getRequestURI();
        System.out.println("RequestURI:" + request.getRequestURI());
        //url:请求访问全路径    http://101.37.252.177:8082/yuyue-app/share/WXShare
        request.getRequestURL();
        System.out.println("RequestURL:" + request.getRequestURL());
        //路径参数
        String text = request.getParameter("text");
        System.out.println(text);


        //start
        request.getParameter("url");

        ReturnResult returnResult=new ReturnResult();
        returnResult.setResult(null);
        returnResult.setMessage("分享成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    @RequestMapping("/WXShare")
    @ResponseBody
    public JSONObject wxShare(HttpServletRequest request){
        ReturnResult returnResult =new ReturnResult();
        WXShare wxShare= new WXShare();
        // 微信 appId
        String appid = "wx82e0374be0e044a4";
        // 微信secret  密钥
        String secret = "c08075181dce2ffe3f036734f168318f";

        // 初始化access_token
        String access_token = "";
        //  获取URL 这里的URL指的是需要分享的那个页面地址,建议这里不要写成固定地址，而是获取当前地址.
        String url = request.getParameter("url");


        if (redisUtil.existsKey("WX_access_token")){
//=========================================================获取ticket=======================================================
             access_token =JSONObject.toJSONString(redisUtil.getString("WX_access_token"));
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi"
                    .replace("ACCESS_TOKEN", access_token);
            System.out.println("-------------------------------");
            System.out.println("redis获取access_token: "+access_token);
            // 访问外部链接 获取凭证
            JSONObject returnResultTicket = HttpAccessUtil.getReturnResult(requestUrl);
            if(StringUtils.isNotNull(returnResultTicket)){
                try {
                    //获取票
                    String ticket = returnResultTicket.getString("ticket");
                    System.out.println("-------------------------------");
                    System.out.println("获取的票ticket: "+ticket);
                    //随机数
                    String noncestr = RandomSaltUtil.generetRandomSaltCode(7);
                    //时间戳
                    String timestamp = Long.toString(System.currentTimeMillis() / 1000);
                    //url参数
                    /* String string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url="+ url;*/

                    String param = "jsapi_ticket="+ticket+"&amp;noncestr="+noncestr+"×tamp="+timestamp+"&amp;url="+url;
                    //参数加密  签名
                    String signature = DigestUtils.md5Hex(param);
                    System.out.println("-------------------------------");
                    System.out.println("获取的签名signature:"+signature);

                    Map<String, String> map = new HashMap<>();
                    map.put("url", url);
                    map.put("jsapi_ticket", ticket);
                    map.put("nonceStr", noncestr);
                    map.put("timestamp", timestamp);
                    map.put("signature", signature);


                    wxShare.setTicket(map.get("jsapi_ticket"));
                    wxShare.setSignature(map.get("signature"));
                    wxShare.setNoncestr(map.get("nonceStr"));
                    wxShare.setTimestamp(map.get("timestamp"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                returnResult.setMessage("获取ticket失败！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }else {
//=========================================================获取token=======================================================
            // 创建通过Api获取Token的链接与参数
            String requestTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET"
                    .replace("APPID", appid)
                    .replace("SECRET", secret);
            //获取token的链接,通过该外部链接可以得到token
            JSONObject returnResultToken = HttpAccessUtil.getReturnResult(requestTokenUrl);
            if (StringUtils.isNotNull(returnResultToken)){
                // 获取Token值
                access_token = returnResultToken.getString("access_token");
                System.out.println("-------------------------------");
                System.out.println("所获取的token:"+access_token);
                redisUtil.setString("WX_access_token",access_token,7200);

                // 获取Token有效期值
                Long expires_in = returnResultToken.getLong("expires_in");
                System.out.println("expires_in:"+expires_in);

                // 创建日期赋值为当前日期
                Long createDate = new Date().getTime()/1000;
            }else {
                returnResult.setMessage("获取access_token失败！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }

        returnResult.setMessage("成功获取凭证！！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(wxShare);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}

