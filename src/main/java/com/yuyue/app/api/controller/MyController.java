package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.MyService;
import com.yuyue.app.utils.RandomSaltUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 我的页面
 */

@RestController
@RequestMapping(value = "/myController" ,produces = "application/json; charset=UTF-8")
public class MyController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(MyController.class);

    @Autowired
    private MyService myService;



    /**
     * 意见反馈提交
     * @param request
     * @return
     */
    @RequestMapping("/feedback")
    @ResponseBody
    @LoginRequired
    public JSONObject addBarrages(@CurrentUser AppUser user, HttpServletRequest request){
        Map<String, String> mapValue = getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        Feedback feedback = new Feedback();
        feedback.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
        feedback.setContact(mapValue.get("contact"));
        feedback.setPictureUrl(mapValue.get("pictureUrl"));
        feedback.setDetails(mapValue.get("details"));
        feedback.setUserId(user.getId());
        myService.insertFeedback(feedback);
        returnResult.setMessage("添加成功！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 充值记录
     * @return
     */
    @RequestMapping("/getMoneyList")
    @ResponseBody
    @LoginRequired
    public JSONObject getMoneyList(@CurrentUser AppUser user){
        List<Order> list = myService.getMoneyList(user.getId());
        ReturnResult returnResult=new ReturnResult();
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无充值记录！");
        } else {
            returnResult.setMessage("查询成功！");
        }
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONArray.parseArray(JSON.toJSONString(list)));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    @RequestMapping("/addAdvertisemenInfo")
    @ResponseBody
    public JSONObject addAdvertisemenInfo(HttpServletRequest request){
        Map<String, String> parameterMap = getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        String userId=parameterMap.get("userId");
        String merchantAddr=parameterMap.get("merchantAddr");
        String businessLicense=parameterMap.get("businessLicense");
        String IdCard=parameterMap.get("IdCard");
        String agencyCode=parameterMap.get("agencyCode");
        String merchantName=parameterMap.get("merchantName");
        String phone=parameterMap.get("phone");
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(merchantAddr) || StringUtils.isEmpty(businessLicense)  || StringUtils.isEmpty(IdCard)
                || StringUtils.isEmpty(agencyCode) || StringUtils.isEmpty(merchantName) || StringUtils.isEmpty(phone) ){
            returnResult.setMessage("必填项存在空值");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Advertisement advertisement=new Advertisement();
        advertisement.setId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        //必填的属性
        advertisement.setUserId(userId);
        advertisement.setMerchantAddr(merchantAddr);
        advertisement.setBusinessLicense(businessLicense);
        advertisement.setIdCard(IdCard);
        advertisement.setAgencyCode(agencyCode);
        advertisement.setMerchantName(merchantName);
        advertisement.setPhone(phone);

        //选填的属性
        advertisement.setProduceAddr(parameterMap.get("produceAddr"));
        advertisement.setFixedPhone(parameterMap.get("fixedPhone"));
        advertisement.setEmail(parameterMap.get("email"));
        advertisement.setWx(parameterMap.get("wx"));
        advertisement.setQqNum(parameterMap.get("qqNum"));
        advertisement.setMerchandiseUrl(parameterMap.get("merchandiseUrl"));
        advertisement.setTelephone(parameterMap.get("telephone"));
        System.out.println(advertisement);
        myService.addAdvertisemenInfo(advertisement);
        returnResult.setMessage("信息插入成功");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }



    @RequestMapping("/getAdvertisementInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getAdvertisementInfo(@CurrentUser AppUser appUser){
        ReturnResult returnResult =new ReturnResult();
        returnResult.setResult(myService.getAdvertisementInfo(appUser.getId()));
        returnResult.setMessage("信息返回成功");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }



}
