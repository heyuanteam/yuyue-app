package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;

public interface LoginService {

    //查詢版本
    AppVersion getAppVersion(String appVersion);

    //根据手机号查询
    AppUser getAppUserMsg(String password,String phone,String id);

    void addUser(AppUser appUser);

    void editPassword(String phone,String password);

    //获取token
    String getToken(AppUser user);

    void updateAppUser(String id, String nickName, String realName, String idCard, String phone, String sex,
                       String headpUrl, String userStatus, String addrDetail, String education, String wechat,
                       String signature, String userUrl, String cardZUrl, String cardFUrl, String ciphertextPwd);
}
