package com.yuyue.app.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.mapper.AppVersionMapper;
import com.yuyue.app.api.mapper.LoginMapper;
import com.yuyue.app.api.service.LoginService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;


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


    public AppVersion getAppVersion(String systemType) {
        return appVersionMapper.getAppVersion(systemType);
    }

    @Override
    public AppUser getAppUserMsg(String password, String phone, String id) { return loginMapper.getAppUserMsg(password,phone,id); }

    /**
     * 通过用户名模糊查询用户信息
     * @param userId
     * @param content
     * @return
     */
    @Override
    public List<AppUser> getAppUserMsgToLike(String userId, String content){
        return loginMapper.getAppUserMsgToLike(userId,content);
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

    @Override
    public void updateAppUser(String id, String nickName, String realName, String idCard, String phone, String sex,
                              String headpUrl, String userStatus, String addrDetail, String education, String wechat,
                              String signature, String userUrl, String cardZUrl, String cardFUrl, String ciphertextPwd,
                              String city, String jpushName, String opendId, String wechatName,String frontCover) {
        loginMapper.updateAppUser(id,nickName,realName,idCard,phone,sex,headpUrl, userStatus, addrDetail, education,
                wechat,signature,userUrl,cardZUrl,cardFUrl,ciphertextPwd,city,jpushName,opendId,wechatName,frontCover);
    }

    @Override
    public void updateOpendId(String id, String opendId, String wechatName) {
        loginMapper.updateOpendId(id,opendId,wechatName);
    }

}
