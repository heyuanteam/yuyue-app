package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.mapper.AppVersionMapper;
import com.yuyue.app.api.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Override
    public AppVersion getAppVersion(String appVersion) {
        return appVersionMapper.getAppVersion(appVersion);
    }
}
