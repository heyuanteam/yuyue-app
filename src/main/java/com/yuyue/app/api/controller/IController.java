package com.yuyue.app.api.controller;

import com.yuyue.app.utils.SmsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 默认页面
 */
@Controller
public class IController {

    @RequestMapping(value="/", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String hello() {
        return "Hello ！ 娱悦APP ！";
    }
}
