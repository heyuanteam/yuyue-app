package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录模块
 */
@RestController
@RequestMapping(value="/login", produces = "application/json; charset=UTF-8")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @ResponseBody
    @RequestMapping( "/version")
    public JSONObject getUserName(@RequestParam(value = "appVersion") String appVersion) {
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isEmpty(appVersion)){
            jsonObject.put("code","01");
            jsonObject.put("result","版本号为空！");
            return jsonObject;
        }
        AppVersion version = loginService.getAppVersion(appVersion);
        if (version == null){
            jsonObject.put("code","01");
            jsonObject.put("result","请设置版本号！");
            return jsonObject;
        }

        jsonObject.put("code","00");
        jsonObject.put("result",version.toString());
        return jsonObject;
    }

}
