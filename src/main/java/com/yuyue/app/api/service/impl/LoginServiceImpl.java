package com.yuyue.app.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.mapper.AppVersionMapper;
import com.yuyue.app.api.mapper.LoginMapper;
import com.yuyue.app.api.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


/**
 * @author ly
 */
@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {
    private Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private AppVersionMapper appVersionMapper;
    @Autowired
    private LoginMapper loginMapper;


    public AppVersion getAppVersion(String appVersion) {
        return appVersionMapper.getAppVersion(appVersion);
    }

    @Override
    public AppUser getAppUserMsgByPhone(String phone) {
        return loginMapper.getAppUserMsg(phone,null);
    }

    @Override
    public AppUser getAppUserById(String id) {
        return loginMapper.getAppUserMsg(null,id);
    }

    @Override
    public void addUser(AppUser appUser) {
        loginMapper.addUser(appUser);
    }

    @Override
    public void editPassword(String phone,String password) { loginMapper.editPassword(phone,password); }

    @Override
    public String getToken(AppUser appUser) {
        String token = "";
        try {
            token = JWT.create()
                    .withAudience(appUser.getId().toString())          // 将 user id 保存到 token 里面
                    .sign(Algorithm.HMAC256(appUser.getPassword()));   // 以 password 作为 token 的密钥
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
            LOGGER.info("token生成错误！" );
        }
        return token;
    }
}
