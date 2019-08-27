package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 登录模块
 */
@RestController
@RequestMapping(value="/login", produces = "application/json; charset=UTF-8")
public class LoginController {
    private static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisTemplate redisTemplate;

    private ReturnResult result = new ReturnResult();

    /**
     * 获取版本号
     *
     * @param appVersion
     * @return
     */
    @ResponseBody
    @RequestMapping("/version")
    public JSONObject getVersion(@RequestParam(value = "appVersion") String appVersion) {
        try {
            if (StringUtils.isEmpty(appVersion)) {
                result.setMessage("版本号为空！");
            } else {
                AppVersion version = loginService.getAppVersion(appVersion);
                if (version == null) {
                    result.setMessage("请设置版本号！");
                } else {
                    result.setMessage("访问成功!");
                    result.setStatus(Boolean.TRUE);
                    result.setResult(JSONObject.toJSON(version));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("版本号查询失败！");
            LOGGER.info("版本号查询失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 用户使用账号密码登录功能
     *
     * @param password
     * @param phone
     * @return
     * @throws Exception
     */
    @RequestMapping("/loginByPassword")
    @ResponseBody
    public JSONObject loginByPassword(@RequestParam(value = "password") String password,
                                      @RequestParam(value = "phone") String phone) throws Exception {
        try {
            if (StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)) {
                result.setMessage("账号密码不能为空!");
            } else {
                AppUser appUser = loginService.getAppUserMsg("",phone,"");
                if (appUser == null) {
                    result.setMessage("该用户未注册!");
                } else {
                    String ciphertextPwd = MD5Utils.getMD5Str(password + appUser.getSalt());
                    if (!ciphertextPwd.equals(appUser.getPassword())) {
                        result.setMessage("账号或密码不正确!");
                    } else {
                        result.setMessage("登录成功！");
                        result.setStatus(Boolean.TRUE);
                        result.setToken(loginService.getToken(appUser));
                        result.setResult(JSONObject.toJSON(appUser));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("账号密码登录失败！");
            LOGGER.info("账号密码登录失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 用户修改账号密码功能
     *
     * @param password
     * @param phone
     * @return
     * @throws Exception
     */
    @RequestMapping("/editPassword")
    @ResponseBody
    public JSONObject editPassword(@RequestParam(value = "password") String password, @RequestParam(value = "code") String code,
                                   @RequestParam(value = "phone") String phone) throws Exception {
        try {
            if (StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)) {
                result.setMessage("账号密码不能为空!");
            } else if (!code.equals(redisTemplate.opsForValue().get(phone).toString())) {
                result.setMessage("验证码错误！");
            } else {
                AppUser appUser = loginService.getAppUserMsg("",phone,"");
                if (appUser == null) {
                    result.setMessage("该用户未注册!");
                } else {
                    String ciphertextPwd = MD5Utils.getMD5Str(password + appUser.getSalt());
                    loginService.editPassword(phone, ciphertextPwd);
                    result.setMessage("修改密码成功！");
                    result.setStatus(Boolean.TRUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("修改密码失败！");
            LOGGER.info("修改密码失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 用户通过手机号及验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    @RequestMapping("/loginByPhone")
    @ResponseBody
    public JSONObject loginByPhone(@RequestParam(value = "phone") String phone, @RequestParam("code") String code) {
        try {
            if (StringUtils.isEmpty(code)) {
                result.setMessage("验证码为空！");
            } else if (!code.equals(redisTemplate.opsForValue().get(phone).toString())) {
                result.setMessage("验证码错误！");
            } else {
                AppUser appUser = loginService.getAppUserMsg("",phone,"");
                if (appUser == null) {
                    result.setMessage("请您先去注册！");
                } else {
                    result.setMessage("登录成功！");
                    result.setStatus(Boolean.TRUE);
                    result.setToken(loginService.getToken(appUser));
                    result.setResult(JSONObject.toJSON(appUser));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("手机号及验证码登录失败！");
            LOGGER.info("手机号及验证码登录失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 用户注册
     *
     * @param phone
     * @param code
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping("/regist")
    @ResponseBody
    public JSONObject regist(@RequestParam(value = "phone") String phone, @RequestParam("code") String code, @RequestParam(value = "password") String password) throws Exception {

        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$");
        try {
            if (!code.equals(redisTemplate.opsForValue().get(phone).toString())) {
                result.setMessage("验证码错误！");
            } else if (pattern.matcher(phone).matches() == false || phone.length() != 11) {
                result.setMessage("手机号输入错误！");
            } else {
                AppUser appUserMsgByPhone = loginService.getAppUserMsg("",phone,"");
                if (appUserMsgByPhone != null) {
                    result.setMessage("该号码已注册！");
                } else {
                    //uuid
                    String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                    String salt = RandomSaltUtil.generetRandomSaltCode(4);
                    AppUser appUser = new AppUser();
                    appUser.setId(uuid);
                    appUser.setUserNo(RandomSaltUtil.randomNumber(15));
                    appUser.setNickName(phone);
                    appUser.setRealName(phone);
                    appUser.setPhone(phone);
                    appUser.setPassword(MD5Utils.getMD5Str(password + salt));
                    appUser.setSalt(salt);//盐
                    loginService.addUser(appUser);
                    result.setMessage("注册成功！");
                    result.setStatus(Boolean.TRUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("用户注册失败！");
            LOGGER.info("用户注册失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 获取实时信息
     *
     * @return
     */
    @RequestMapping("/getMessage")
    @ResponseBody
    public JSONObject getMessage(String id) {
        AppUser appUserById = loginService.getAppUserMsg("","",id);
        if (appUserById == null) {
            result.setMessage("查询数据失败！");
        } else {
            result.setMessage("获取成功！");
            result.setStatus(Boolean.TRUE);
            result.setToken(loginService.getToken(appUserById));
            result.setResult(JSONObject.toJSON(appUserById));
        }
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 修改信息
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/updateAppUser", produces = "application/json; charset=UTF-8")
    @ResponseBody
    @LoginRequired
    public JSONObject updateAppUser(@CurrentUser AppUser user, String nickName, String realName, String idCard, String phone,
                                    String sex, String headpUrl, String userStatus, String addrDetail, String education, String wechat,
                                    String signature, String password, String oldPassword,String code,
                                    String userUrl, String cardZUrl, String cardFUrl) throws Exception {
        String ciphertextPwd = "";
        if (StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(oldPassword)) {
            oldPassword = MD5Utils.getMD5Str(oldPassword + user.getSalt());
            AppUser appUserMsg = loginService.getAppUserMsg(oldPassword, "", "");
            if(appUserMsg == null){
                result.setMessage("修改失败！旧密码！");
                return ResultJSONUtils.getJSONObjectBean(result);
            }
            ciphertextPwd = MD5Utils.getMD5Str(password + user.getSalt());
        } else if (StringUtils.isNotEmpty(phone)){
            if(StringUtils.isEmpty(code)){
                result.setMessage("验证码为空！");
                return ResultJSONUtils.getJSONObjectBean(result);
            } else if(!code.equals(redisTemplate.opsForValue().get(phone).toString())) {
                result.setMessage("验证码错误！");
                return ResultJSONUtils.getJSONObjectBean(result);
            }
        }
        AppUser appUserById = loginService.getAppUserMsg("","",user.getId());
        if (appUserById == null) {
            result.setMessage("修改失败！该用户不存在！");
            return ResultJSONUtils.getJSONObjectBean(result);
        }
        LOGGER.info("============" + user.toString());
        loginService.updateAppUser(user.getId(),nickName,realName,idCard,phone,sex,headpUrl, userStatus,
                            addrDetail, education,wechat,signature,userUrl,cardZUrl,cardFUrl,ciphertextPwd,"","");
        result.setMessage("修改成功！");
        result.setStatus(Boolean.TRUE);
        AppUser appUser = loginService.getAppUserMsg("","",user.getId());
        result.setToken(loginService.getToken(appUser));
        result.setResult(JSONObject.toJSON(appUser));
        return ResultJSONUtils.getJSONObjectBean(result);
    }

    /**
     * 获取定位和极光别名，ID
     *
     * @return
     */
    @RequestMapping("/getJPush")
    @ResponseBody
    public JSONObject getJPush(String id,String city,String jpushName) {
        AppUser appUserById = loginService.getAppUserMsg("","",id);
        if (appUserById != null) {
            loginService.updateAppUser(appUserById.getId(),"","","","","","",
                    "","","","","","","","",
                    "",city,jpushName);
            result.setMessage("获取成功！");
            result.setStatus(Boolean.TRUE);
            result.setToken(loginService.getToken(appUserById));
            result.setResult(JSONObject.toJSON(appUserById));
        }
        result.setMessage("获取成功！");
        result.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(result);
    }
}

