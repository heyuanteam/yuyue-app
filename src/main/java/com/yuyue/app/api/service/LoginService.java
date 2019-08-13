package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;

public interface LoginService {

    //查詢版本
    AppVersion getAppVersion(String appVersion);

    //根据手机号查询
    AppUser getAppUserMsgByPhone(String phone);

    //根据ID查询
    AppUser getAppUserById(String id);

    void addUser(AppUser appUser);

    void editPassword(String phone,String password);

    //获取token
    String getToken(AppUser user);
}
