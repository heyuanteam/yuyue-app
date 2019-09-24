package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.utils.MD5Utils;
import com.yuyue.app.utils.RandomSaltUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 登录模块
 */
@RestController
@RequestMapping(value="/login", produces = "application/json; charset=UTF-8")
public class LoginController extends BaseController{
    private static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取版本号
     *
     * @param appVersion
     * @return
     */
    @ResponseBody
    @RequestMapping("/version")
    public JSONObject getVersion(@RequestParam(value = "appVersion") String appVersion, HttpServletRequest request) {
        LOGGER.info("获取版本号-------------->>/login/version");
        getParameterMap(request);
        ReturnResult result = new ReturnResult();
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
    public JSONObject loginByPassword(@RequestParam(value = "password") String password, @RequestParam(value = "phone") String phone,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        LOGGER.info("用户使用账号密码登录功能-------------->>/login/loginByPassword");
        getParameterMap(request);
        ReturnResult result = new ReturnResult();
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
                                   @RequestParam(value = "phone") String phone,HttpServletRequest request) throws Exception {
        LOGGER.info("用户修改账号密码功能-------------->>/login/editPassword");
        getParameterMap(request);
        ReturnResult result = new ReturnResult();
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
    public JSONObject loginByPhone(@RequestParam(value = "phone") String phone, @RequestParam("code") String code,
                                   HttpServletRequest request, HttpServletResponse response) {
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        LOGGER.info("用户通过手机号及验证码登录-------------->>/login/loginByPhone");
        getParameterMap(request);
        ReturnResult result = new ReturnResult();
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
    @RequestMapping("/registration")
    @ResponseBody
    public JSONObject registration(@RequestParam(value = "phone") String phone,HttpServletRequest request,@RequestParam("code") String code, @RequestParam(value = "password") String password) throws Exception {
        ReturnResult result = new ReturnResult();
        LOGGER.info("用户注册-------------->>/login/registration");
        getParameterMap(request);
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$");
        try {
            if(StringUtils.isEmpty(code) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)){
                result.setMessage("参数错误！");
            } else if (!code.equals(redisTemplate.opsForValue().get(phone).toString())) {
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
    @LoginRequired
    public JSONObject getMessage(@CurrentUser AppUser user,HttpServletRequest request) {
        LOGGER.info("获取实时信息-------------->>/login/getMessage");
        getParameterMap(request);
        ReturnResult result = new ReturnResult();
        if(StringUtils.isEmpty(user.getId())){
            result.setMessage("缺少用户id!!!");
            return ResultJSONUtils.getJSONObjectBean(result);
        }
        AppUser appUserById = loginService.getAppUserMsg("","",user.getId());
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
                                    String userUrl, String cardZUrl, String cardFUrl,HttpServletRequest request) throws Exception {
        LOGGER.info("修改信息-------------->>/login/updateAppUser");
        getParameterMap(request);
        String ciphertextPwd = "";
        ReturnResult result = new ReturnResult();
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
        } else if (StringUtils.isNotEmpty(idCard)){
            if(idCard.length() < 18){
                result.setMessage("身份证号码错误！");
                return ResultJSONUtils.getJSONObjectBean(result);
            }
        }
        AppUser appUserById = loginService.getAppUserMsg("","",user.getId());
        if (appUserById == null) {
            result.setMessage("修改失败！该用户不存在！");
            return ResultJSONUtils.getJSONObjectBean(result);
        }
        LOGGER.info("============" + user.toString());
        loginService.updateAppUser(user.getId(),nickName,realName,idCard,phone,sex,headpUrl, userStatus, addrDetail, education,
                wechat,signature,userUrl,cardZUrl,cardFUrl,ciphertextPwd,"","","","","");
        result.setMessage("修改成功！");
        result.setStatus(Boolean.TRUE);
        AppUser appUser = loginService.getAppUserMsg("","",user.getId());
        result.setToken(loginService.getToken(appUser));
        result.setResult(JSONObject.toJSON(appUser));
        return ResultJSONUtils.getJSONObjectBean(result);
    }
    /**
     * 修改信息
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/userAuthentication")
    @ResponseBody
    @LoginRequired
    public JSONObject userAuthentication(@CurrentUser AppUser user, String realName, String idCard,
                                         String userUrl, String cardZUrl, String cardFUrl,HttpServletRequest request) throws Exception {
        LOGGER.info("用户认证-------------->>/login/userAuthentication");
        getParameterMap(request);
        ReturnResult returnResult = new ReturnResult();
        if (StringUtils.isEmpty(realName)){
            returnResult.setMessage("用户真实姓名不能为空");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if(StringUtils.isEmpty(idCard)){
            returnResult.setMessage("用户身份证id不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if(StringUtils.isEmpty(userUrl)){
            returnResult.setMessage("用户正面照不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if(StringUtils.isEmpty(cardZUrl)){
            returnResult.setMessage("用户身份证正面不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if(StringUtils.isEmpty(cardFUrl)){
            returnResult.setMessage("用户身份证反面不能为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Pattern pattern = Pattern.compile("^\\d{6}(18|19|20)?\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|[xX])$");
       // Pattern pattern = Pattern.compile("\\\\d{15}(\\\\d{2}[0-9xX])?");
        if (pattern.matcher(idCard).matches() == false || idCard.length() != 18) {
            returnResult.setMessage("身份证号输入错误！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        loginService.userAuthentication(user.getId(),realName,idCard,userUrl,cardZUrl,cardFUrl);
        returnResult.setMessage("认证成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    /**
     * 获取定位和极光别名，ID
     *
     * @return
     */
    @RequestMapping("/getJPush")
    @ResponseBody
    public JSONObject getJPush(String id,String city,String jpushName,HttpServletRequest request) {
        LOGGER.info("获取定位和极光别名-------------->>/login/getJPush");
        getParameterMap(request);
        ReturnResult result = new ReturnResult();
        AppUser appUserById = loginService.getAppUserMsg("","",id);
        if (appUserById != null) {
            loginService.updateAppUser(appUserById.getId(),"","","","","","",
                    "","","","","","","","",
                    "",city,jpushName,"","","");
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

