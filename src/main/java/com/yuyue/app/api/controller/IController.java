package com.yuyue.app.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 默认页面
 */
@Controller
public class IController {

    @RequestMapping("/")
    @ResponseBody
    public String hello(){
        return "Hello ！ 娱悦APP ！";
    }
}
