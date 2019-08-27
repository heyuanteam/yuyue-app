package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.domain.Feedback;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.MyService;
import com.yuyue.app.utils.ResultJSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    private ReturnResult returnResult=new ReturnResult();

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
}
