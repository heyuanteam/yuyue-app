package com.yuyue.app.api.controller;

import com.yuyue.app.api.mapper.UserMapper;
import com.yuyue.app.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by XieZhiXin on 2018/8/8.
 */
@RestController
@RequestMapping(value="/mybatis", produces = "application/json; charset=UTF-8")
public class MybatisController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapperDao;

    @GetMapping( "/user/{userid}")
    public String getUserName(@PathVariable(value = "userid") Integer userid) {

        String  name=userService.getUserName(userid);
        //String  nam=userMapperDao.getUserNameById(userid);
        return "获取到的用户姓名为："+name;
    }

}
