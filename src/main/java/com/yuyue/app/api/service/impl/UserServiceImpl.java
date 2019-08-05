package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.mapper.UserMapper;
import com.yuyue.app.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by XieZhiXin on 2018/8/8.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapperDao;

    @Override
    public String getUserName(int stu_id)
    {
        String  name=userMapperDao.getUserNameById(stu_id);
        return name;
    }
}
