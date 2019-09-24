package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;

import java.util.List;

public interface LoginService {
    //通过用户名模糊查询用户信息
    List<AppUser> getAppUserMsgToLike(String userId, String content);

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
                       String signature, String userUrl, String cardZUrl, String cardFUrl, String ciphertextPwd,
                       String city,String jpushName,String opendId,String wechatName,String frontCover);
    void userAuthentication(String userId,String realName, String idCard,
                                   String userUrl, String cardZUrl, String cardFUrl);
    void updateOpendId(String id, String opendId, String wechatName);
}
