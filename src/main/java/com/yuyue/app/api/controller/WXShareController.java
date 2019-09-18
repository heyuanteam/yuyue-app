package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.api.service.UserCommentService;
import com.yuyue.app.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/share" ,method = RequestMethod.GET,produces = "application/json; charset=UTF-8")
public class WXShareController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(WXShareController.class);
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private LoginService loginService;



    /**
     *测试
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

    /**
     * web网页分享；获取微信分享 signature，nonceStr，timestamp 和appId
     * @param request
     * @return
     */
    @RequestMapping("/WX_WEB_Share")
    @ResponseBody
    public JSONObject wxShare(HttpServletRequest request){

        ReturnResult returnResult =new ReturnResult();
        WXShare wxShare= new WXShare();
        // 微信 appId
        String appId = "wx82e0374be0e044a4";
        // 微信secret  密钥
        String secret = "c08075181dce2ffe3f036734f168318f";

        // 初始化access_token
        String access_token = "";
        //  获取URL 这里的URL指的是需要分享的那个页面地址,建议这里不要写成固定地址，而是获取当前地址.
        String url = request.getParameter("url");
        System.out.println(url);

//=========================================================获取token=======================================================

        if (redisUtil.existsKey("WX_access_token")){
            String redis_access_token =JSONObject.toJSONString(redisUtil.getString("WX_access_token"));
            System.out.println(redis_access_token);
             access_token = redis_access_token.substring(1,redis_access_token.length()-1);
            /* redisUtil.deleteKey("WX_access_token");*/
            System.out.println("-------------------------------");
            System.out.println("redis中所获取的token:"+access_token);
        }else {
           // 创建通过Api获取Token的链接与参数
            String requestTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET"
                    .replace("APPID", appId)
                    .replace("SECRET", secret);
            //获取token的链接,通过该外部链接可以得到token
            JSONObject returnResultToken = HttpUtils.getReturnResult(requestTokenUrl);
            if (StringUtils.isNotNull(returnResultToken)){
                // 获取Token值
                access_token = returnResultToken.getString("access_token");
                System.out.println("-------------------------------");
                System.out.println("所获取的token:"+access_token);
                redisUtil.setString("WX_access_token",access_token,7000);

                // 获取Token有效期值
                Long expires_in = returnResultToken.getLong("expires_in");
                System.out.println("expires_in:"+expires_in);

            }else {
                returnResult.setMessage("获取access_token失败！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
//=========================================================获取ticket=======================================================
        // 获取凭证的访问链接77777
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi"
                .replace("ACCESS_TOKEN", access_token);

        JSONObject returnResultTicket = HttpUtils.getReturnResult(requestUrl);
        //获取凭证
        String ticket = returnResultTicket.getString("ticket");
        System.out.println("-------------------------------");
        System.out.println("获取的票ticket: "+ticket);
        if(StringUtils.isNotEmpty(ticket)){
            try {

                //随机数
                String noncestr = RandomSaltUtil.generetRandomSaltCode(7);
                //时间戳   创建日期赋值为当前日期
                String timestamp = Long.toString(System.currentTimeMillis() / 1000);
                //url参数
                /* String string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url="+ url;*/
                String param = "jsapi_ticket="+ticket+"&amp;noncestr="+noncestr+"×tamp="+timestamp+"&amp;url="+url;
                //参数加密  签名
                String signature = DigestUtils.md5Hex(param);
                System.out.println("-------------------------------");
                System.out.println("获取的签名signature:"+signature);

               /* Map<String, String> map = new HashMap<>();
                map.put("url", url);
                map.put("jsapi_ticket", ticket);
                map.put("nonceStr", noncestr);
                map.put("timestamp", timestamp);
                map.put("signature", signature);*/

                wxShare.setAppId(appId);
                wxShare.setTicket(ticket);
                wxShare.setSignature(signature);
                wxShare.setNoncestr(noncestr);
                wxShare.setTimestamp(timestamp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            returnResult.setMessage("获取ticket失败！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

        returnResult.setMessage("成功获取凭证！！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(wxShare);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * APP网页分享
     * @param response
     * @param model
     * @param  request(authorId;videoId;pageSize)
     * @param
     * @param
     * @return
     */
    @RequestMapping("/wxAppShare")
    public String wxAppShare(HttpServletResponse response, Model model,HttpServletRequest request){
        log.info("APP网页分享-------------->>/share/wxAppShare");
        Map<String, String> mapValue = getParameterMap(request);
        response.setHeader("Access-Control-Allow-Origin","*");
        String pageSize = mapValue.get("pageSize");
        String authorId = mapValue.get("authorId");
        String videoId  =  mapValue.get("videoId");
        System.out.println(pageSize+authorId+videoId);
        response.setHeader("Access-Control-Allow-Origin","*");
         int newPageSize = (Integer.parseInt(pageSize) - 1) * 5;
        UploadFile uploadFile = uploadFileService.fileDetail(authorId, videoId);
        AppUser appUserMsg = loginService.getAppUserMsg("", "", authorId);
        List<UserCommentVo> allComment = userCommentService.getCommentByPage(videoId, newPageSize);

        int commentTotal = userCommentService.getCommentTotal(videoId);
        int totalPage = commentTotal / 5;
        if (commentTotal % 5 != 0 ){
            totalPage+=1;
        }

        model.addAttribute("comments",allComment);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("total",commentTotal);
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("authorId",authorId);
        model.addAttribute("videoId",videoId);
        model.addAttribute("appUserMsg",appUserMsg);
        model.addAttribute("uploadFile",uploadFile);
        return  "forward:/share/share.jsp";

    }



}

