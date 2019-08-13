package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 登录模块
 */
@RestController
@RequestMapping(value="/login", produces = "application/json; charset=UTF-8")
public class LoginController {
    private Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ResponseBody
    @RequestMapping( "/version")
    public JSONObject getUserName(@RequestParam(value = "appVersion") String appVersion) {
        ReturnResult result = new ReturnResult();
        try {
            if(StringUtils.isEmpty(appVersion)){
                result.setMessage("版本号为空！");
            } else {
                AppVersion version = loginService.getAppVersion(appVersion);
                if (version == null){
                    result.setMessage("请设置版本号！");
                } else {
                    result.setMessage("访问成功!");
                    result.setStatus(Boolean.TRUE);
                    result.setResult(JSONObject.toJSON(version));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("版本号查询失败！");
            LOGGER.info("版本号查询失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     *
     *  用户使用账号密码登录功能
     * @param password
     * @param phone
     * @return
     * @throws Exception
     */
    @RequestMapping("/loginByPassword")
    @ResponseBody
    public JSONObject loginByPassword(@RequestParam(value = "password")String password,
                                      @RequestParam(value = "phone")String phone) throws Exception {
        ReturnResult result = new ReturnResult();
        try {
            if (StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)){
                result.setMessage("账号密码不能为空!");
            } else {
                AppUser appUser = loginService.getAppUserMsgByPhone(phone);
                if(appUser==null){
                    result.setMessage("该用户未注册!");
                } else {
                    String ciphertextPwd = MD5Utils.getMD5Str(password + appUser.getSalt());
                    if (!ciphertextPwd.equals(appUser.getPassword())){
                        result.setMessage("账号或密码不正确!");
                    } else {
                        result.setMessage("登录成功");
                        result.setStatus(Boolean.TRUE);
                        result.setToken(loginService.getToken(appUser));
                        result.setResult(JSONObject.toJSON(appUser));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("账号密码登录失败！");
            LOGGER.info("账号密码登录失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     *
     *  用户修改账号密码功能
     * @param password
     * @param phone
     * @return
     * @throws Exception
     */
    @RequestMapping("/editPassword")
    @ResponseBody
    public JSONObject editPassword(@RequestParam(value = "password")String password,@RequestParam(value = "code")String code,
                                      @RequestParam(value = "phone")String phone) throws Exception {
        ReturnResult result = new ReturnResult();
        try {
            if (StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)){
                result.setMessage("账号密码不能为空!");
            } else if (!code.equals(redisTemplate.opsForValue().get(phone).toString())){
                result.setMessage("验证码错误！");
            }  else {
                AppUser appUser = loginService.getAppUserMsgByPhone(phone);
                if(appUser==null){
                    result.setMessage("该用户未注册!");
                } else {
                    String ciphertextPwd = MD5Utils.getMD5Str(password + appUser.getSalt());
                    loginService.editPassword(phone,ciphertextPwd);
                    result.setMessage("修改密码成功！");
                    result.setStatus(Boolean.TRUE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("修改密码失败！");
            LOGGER.info("修改密码失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 用户通过手机号及验证码登录
     * @param phone
     * @param code
     * @return
     */
    @RequestMapping("/loginByPhone")
    @ResponseBody
    public JSONObject loginByPhone(@RequestParam(value = "phone")String phone,@RequestParam("code")String code){
        ReturnResult result = new ReturnResult();
        try {
            if (StringUtils.isEmpty(code)){
                result.setMessage("验证码为空！");
            }else if (!code.equals(redisTemplate.opsForValue().get(phone).toString())){
                result.setMessage("验证码错误！");
            } else {
                AppUser appUser = loginService.getAppUserMsgByPhone(phone);
                if(appUser==null){
                    result.setMessage("请您先去注册！");
                } else {
                    result.setMessage("登录成功！");
                    result.setStatus(Boolean.TRUE);
                    result.setToken(loginService.getToken(appUser));
                    result.setResult(JSONObject.toJSON(appUser));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("手机号及验证码登录失败！");
            LOGGER.info("手机号及验证码登录失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
 }

    /**
     * 用户注册
     * @param phone
     * @param code
     * @param password
     * @return
     * @throws Exception
     */
     @RequestMapping("/regist")
     @ResponseBody
    public JSONObject regist(@RequestParam(value = "phone")String phone,@RequestParam("code")String code,@RequestParam(value = "password")String password) throws Exception {

         Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$");
         ReturnResult result = new ReturnResult();
         try {
             if (!code.equals(redisTemplate.opsForValue().get(phone).toString())){
                 result.setMessage("验证码错误！");
             }else if(pattern.matcher(phone).matches() == false || phone.length()!=11){
                 result.setMessage("手机号输入错误！");
             } else {
                 AppUser appUserMsgByPhone = loginService.getAppUserMsgByPhone(phone);
                 if (appUserMsgByPhone!=null){
                     result.setMessage("该号码已注册！");
                 } else {
                     //uuid
                     String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();

                     String salt = RandomSaltUtil.generetRandomSaltCode(4);
                     AppUser appUser=new AppUser();
                     appUser.setId(uuid);
                     appUser.setUserNo(RandomSaltUtil.randomNumber(15));
                     appUser.setNickName(phone);
                     appUser.setRealName(phone);
                     appUser.setPhone(phone);
                     appUser.setPassword(MD5Utils.getMD5Str(password+salt));
                     appUser.setSalt(salt);//盐
                     appUser.setUserStatus("0");
                     loginService.addUser(appUser);
                     result.setMessage("注册成功！");
                     result.setStatus(Boolean.TRUE);
                 }
             }
         }catch (Exception e){
             e.printStackTrace();
             result.setMessage("用户注册失败！");
             LOGGER.info("用户注册失败！");
         }
         return ResultJSONUtils.getJSONObjectBean(result);
    }
}
