package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.google.common.collect.Maps;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.*;
import com.yuyue.app.utils.RandomSaltUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 我的页面
 */

@RestController
@RequestMapping(value = "/myController" ,produces = "application/json; charset=UTF-8")
public class MyController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(MyController.class);

    @Autowired
    private MyService myService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private PayController payController;
    @Autowired
    private PayService payService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private HomePageService homePageService;

    /**
     * 意见反馈提交
     * @param request
     * @return
     */
    @RequestMapping("/feedback")
    @ResponseBody
    public JSONObject addBarrages(HttpServletRequest request, HttpServletResponse response){
        log.info("意见反馈提交-------------->>/myController/feedback");
        Map<String, String> mapValue = getParameterMap(request);
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        ReturnResult returnResult=new ReturnResult();
        String token = request.getHeader("token");
        String userId = "";
        if(StringUtils.isNotEmpty(token)){
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
        }
        if(StringUtils.isEmpty(mapValue.get("contact")) || StringUtils.isEmpty(mapValue.get("pictureUrl"))
                || StringUtils.isEmpty(mapValue.get("details")) ){
            returnResult.setMessage("参数为空！");
        } else {
            Feedback feedback = new Feedback();
            feedback.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
            feedback.setContact(mapValue.get("contact"));
            feedback.setPictureUrl(mapValue.get("pictureUrl"));
            feedback.setDetails(mapValue.get("details"));
            feedback.setUserId(userId);
            Feedback feed = myService.getFeedback(feedback.getDetails());
            if(StringUtils.isNotNull(feed)){
                returnResult.setMessage("请勿重复提交！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
            myService.insertFeedback(feedback);
            returnResult.setMessage("反馈成功！");
            returnResult.setStatus(Boolean.TRUE);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 充值记录和送礼记录
     * @return
     */
    @RequestMapping("/getMoneyList")
    @ResponseBody
    @LoginRequired
    public JSONObject getMoneyList(@CurrentUser AppUser user,HttpServletRequest request){
        log.info("充值记录和送礼记录-------------->>/myController/getMoneyList");
        getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        List<Order> list = myService.getMoneyList(user.getId());
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无消费记录！");
        } else {
            returnResult.setMessage("查询成功！");
        }
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONArray.parseArray(JSON.toJSONString(list)));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 收益记录
     * @return
     */
    @RequestMapping("/changeMoneyList")
    @ResponseBody
    @LoginRequired
    public JSONObject changeMoneyList(@CurrentUser AppUser user,HttpServletRequest request){
        log.info("收益记录-------------->>/myController/changeMoneyList");
        getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        List<ChangeMoneyVo> list = myService.changeMoneyList(user.getId());
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无收益记录！");
        } else {
            returnResult.setMessage("查询成功！");
        }
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(JSONArray.parseArray(JSON.toJSONString(list)));
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 广告推广申请
     * @param request
     * @return
     */
    @RequestMapping("/addAdvertisementInfo")
    @ResponseBody
    public JSONObject addAdvertisementInfo(HttpServletRequest request, HttpServletResponse response){
        log.info("广告推广申请-------------->>/myController/addAdvertisementInfo");
        getParameterMap(request);
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        ReturnResult returnResult=new ReturnResult();
        String token = request.getHeader("token");
        String userId = "";
        if(StringUtils.isNotEmpty(token)){
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
            Advertisement advertisementInfo = myService.getAdvertisementInfo(userId);
            if(StringUtils.isNotNull(advertisementInfo)){
                if("10B".equals(advertisementInfo.getStatus())){
                    returnResult.setMessage("审核通过！！");
                    returnResult.setStatus(Boolean.TRUE);
                }else {
                    returnResult.setMessage("已提交，待审核");
                    returnResult.setStatus(Boolean.TRUE);
                }
                returnResult.setResult(advertisementInfo);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }
        Map<String, String> parameterMap = getParameterMap(request);
        //商家地址
        String merchantAddr=parameterMap.get("merchantAddr");
        //营业执照
        String businessLicense=parameterMap.get("businessLicense");
        //法人身份证正面
        String idCardZM=parameterMap.get("idCardZM");
        //法人身份证反面
        String idCardFM=parameterMap.get("idCardFM");
        //机构代码
        String agencyCode=parameterMap.get("agencyCode");
        //商家名称
        String merchantName=parameterMap.get("merchantName");
        //手机号码
        String phone=parameterMap.get("phone");
        if(StringUtils.isEmpty(merchantAddr) ){
            returnResult.setMessage("商家地址未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (StringUtils.isEmpty(businessLicense)){
            returnResult.setMessage("营业执照未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (StringUtils.isEmpty(idCardZM)){
            returnResult.setMessage("法人身份证正面未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (StringUtils.isEmpty(idCardFM) ){
            returnResult.setMessage("法人身份证反面未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (StringUtils.isEmpty(agencyCode)){
            returnResult.setMessage("机构代码未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if ( StringUtils.isEmpty(merchantName)){
            returnResult.setMessage("商家名称未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if ( StringUtils.isEmpty(phone)){
            returnResult.setMessage("手机号码未空值!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Advertisement advertisement=new Advertisement();
        advertisement.setId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        //必填的属性
        advertisement.setUserId(userId);
        advertisement.setMerchantAddr(merchantAddr);
        advertisement.setBusinessLicense(businessLicense);
        advertisement.setIdCardZM(idCardZM);
        advertisement.setIdCardFM(idCardFM);
        advertisement.setAgencyCode(agencyCode);
        advertisement.setMerchantName(merchantName);
        advertisement.setPhone(phone);

        //选填的属性
        advertisement.setProduceAddr(parameterMap.get("produceAddr"));
        advertisement.setFixedPhone(parameterMap.get("fixedPhone"));
        advertisement.setEmail(parameterMap.get("email"));
        advertisement.setWx(parameterMap.get("wx"));
        advertisement.setQqNum(parameterMap.get("qqNum"));
        advertisement.setMerchandiseUrl(parameterMap.get("merchandiseUrl"));
        advertisement.setTelephone(parameterMap.get("telephone"));

        Advertisement adver = myService.findAdvertisement(advertisement.getAgencyCode());
        if(StringUtils.isNotNull(adver)){
            returnResult.setMessage("请勿重复添加！");
        } else {
            myService.addAdvertisemenInfo(advertisement);
            returnResult.setMessage("信息插入成功");
            returnResult.setStatus(Boolean.TRUE);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 查看广告推广申请信息
     * @param appUser
     * @return
     */
    @RequestMapping("/getAdvertisementInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getAdvertisementInfo(@CurrentUser AppUser appUser,HttpServletRequest request){
        log.info("查看广告推广申请信息-------------->>/myController/getAdvertisementInfo");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        Advertisement advertisementInfo = myService.getAdvertisementInfo(appUser.getId());
        if (StringUtils.isNull(advertisementInfo)){
            returnResult.setMessage("暂无广告申请信息");
            returnResult.setResult(new Advertisement());
        }else{
            returnResult.setMessage("信息返回成功");
            returnResult.setResult(advertisementInfo);
        }
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 我的评论
     * @param appUser
     * @param videoId
     * @return
     */
    @RequestMapping("/myComments")
    @ResponseBody
    @LoginRequired
    public JSONObject getAllCommentByUserId(@CurrentUser AppUser appUser,String videoId,HttpServletRequest request){
        log.info("我的评论-------------->>/myController/myComments");
        getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        List<UserCommentVo> allComment = userCommentService.getAllComment("", appUser.getId());
        if (StringUtils.isEmpty(allComment)){
            returnResult.setMessage("暂无评论!!");
        }else {
            returnResult.setMessage("返回成功");
        }
        //以后拓展业务,评论中展示视频的名称及图片
        returnResult.setResult(allComment);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }


    /**
     * 演出申请
     * @return
     */
    @RequestMapping("/insertShowName")
    @ResponseBody
    public JSONObject insertShowName(HttpServletRequest request, HttpServletResponse response){
        log.info("演出申请-------------->>/myController/insertShowName");
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        String token = request.getHeader("token");
        Map<String, String> mapValue = getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        String userId = "";
        if(StringUtils.isNotEmpty(token)) {
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
            ShowName showInfo = myService.getShowInfo(userId);
            if (StringUtils.isNotNull(showInfo)){
                if("10B".equals(showInfo.getStatus())){
                    returnResult.setMessage("审核通过！！");
                    returnResult.setStatus(Boolean.TRUE);
                }else {
                    returnResult.setMessage("已提交，待审核");
                    returnResult.setStatus(Boolean.TRUE);
                }
                returnResult.setResult(showInfo);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
        }if (StringUtils.isEmpty(mapValue.get("teamName")) ){
            returnResult.setMessage("姓名或团队名称参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("size"))){
            returnResult.setMessage("人数参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("address"))){
            returnResult.setMessage("现住地址参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("cardZUrl"))){
            returnResult.setMessage("身份证正面参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("cardFUrl"))){
            returnResult.setMessage("身份证反面参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("categoryId"))){
            returnResult.setMessage("分类ID参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("description"))){
            returnResult.setMessage("节目名称参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("phone"))){
            returnResult.setMessage("手机参数为空！！");
        }else if(StringUtils.isEmpty(mapValue.get("videoAddress"))){
            returnResult.setMessage("视频地址参数为空！！");
        } else {
            ShowName showName = new ShowName();
            showName.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
            showName.setUserId(userId);
            //    姓名或团队名称
            showName.setTeamName(mapValue.get("teamName"));
            //    节目名称
            showName.setDescription(mapValue.get("description"));
            //    人数
            showName.setSize(mapValue.get("size"));
            //    分类ID
            showName.setCategoryId(mapValue.get("categoryId"));
            //    现住地
            showName.setAddress(mapValue.get("address"));
            //    手机
            showName.setPhone(mapValue.get("phone"));
            //    身份证正面
            showName.setCardZUrl(mapValue.get("cardZUrl"));
            //    身份证反面
            showName.setCardFUrl(mapValue.get("cardFUrl"));
            //    视频地址
            showName.setVideoAddress(mapValue.get("videoAddress"));
            //    邮箱
            showName.setMail(mapValue.get("mail"));
            //    微信
            showName.setWeChat(mapValue.get("weChat"));
            ShowName show = myService.findShowName(showName.getDescription(),showName.getPhone());
            if(StringUtils.isNotNull(show)){
                returnResult.setMessage("请勿重复添加！");
            } else {
                myService.insertShowName(showName.getId(),showName.getUserId(),showName.getTeamName(), showName.getSize(),
                        showName.getAddress(),showName.getCardZUrl(),showName.getCardFUrl(),showName.getCategoryId(),
                        showName.getDescription(),showName.getPhone(),
                        showName.getVideoAddress(),showName.getMail(),showName.getWeChat());
                returnResult.setMessage("添加成功！");
                returnResult.setStatus(Boolean.TRUE);
            }
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 查看演出申请信息
     * @param appUser
     * @return
     */
    @RequestMapping("/getShowInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getShowInfo(@CurrentUser AppUser appUser,HttpServletRequest request){
        log.info("查看演出申请信息-------------->>/myController/getShowInfo");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        ShowName showInfo = myService.getShowInfo(appUser.getId());
        if (StringUtils.isNull(showInfo)){
            returnResult.setResult(new ShowName());
            returnResult.setMessage("暂无演出申请！！");
        }else {
            returnResult.setResult(showInfo);
            returnResult.setMessage("信息返回成功");
        }

        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 我的发布
     * @param appUser
     * @return
     */
    @RequestMapping("/myRelease")
    @ResponseBody
    @LoginRequired
    public JSONObject myRelease(@CurrentUser AppUser appUser,HttpServletRequest request){
        log.info("我的发布-------------->>/myController/myRelease");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        Map<String,Object> map = Maps.newHashMap();
        List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthorId(appUser.getId());
        if (StringUtils.isEmpty(videoByAuthorId)){
            returnResult.setMessage("暂无发布视频");
        } else {
            map.put("Author", appUser);
            map.put("videoList", videoByAuthorId);
            returnResult.setMessage("返回成功！！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(map);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    /**
     *商家上传广告申请
     * @param commodity
     */
    @RequestMapping("/commodityToSpread")
    @ResponseBody
    @LoginRequired
    public JSONObject commodityToSpread (Commodity commodity,String tradeType,@CurrentUser AppUser user,HttpServletRequest request){
        log.info("商家上传广告申请-------------->>/myController/commodityToSpread");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        JSONObject jsonObject = null;
        if ( StringUtils.isEmpty(commodity.getCategory())
                ||StringUtils.isEmpty(commodity.getCommodityName())
                ||StringUtils.isEmpty(commodity.getAdWord())
                ||StringUtils.isEmpty(commodity.getAdImageUrl())
                ||StringUtils.isEmpty(commodity.getPayUrl())
                ||StringUtils.isEmpty(commodity.getPriceId())
                ||StringUtils.isEmpty(commodity.getAddr())
                ||StringUtils.isEmpty(tradeType)
                ){
            returnResult.setMessage("上传的8个参数均不可为空！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }if (commodity.getCommodityPrice().signum() == -1){
            returnResult.setMessage("金额输入错误！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            commodity.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());
            commodity.setMerchantId(user.getId());
           /* BigDecimal bds = new BigDecimal(commodity.getAdDuration()).multiply
                    (new BigDecimal(commodity.getAdPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);*/
            List<AdPrice> advertisementFeeInfo = myService.getAdvertisementFeeInfo(commodity.getPriceId());
            if (StringUtils.isEmpty(advertisementFeeInfo)){
                returnResult.setMessage("价格id传入错误！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
            AdPrice adPrice = advertisementFeeInfo.get(0);
            BigDecimal bigDecimal = new BigDecimal(adPrice.getAdTotalPrice()).multiply(new BigDecimal(adPrice.getAdDiscount()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            Order order = new Order();
            order.setTradeType(tradeType);
            order.setMoney(bigDecimal);
            try {
                jsonObject = payController.payYuYue(order, user);
                if ("true".equals(jsonObject.getString("status"))){
                    returnResult.setMessage("订单生成，等待审核！！");
                    returnResult.setStatus(Boolean.TRUE);
                    returnResult.setResult(jsonObject.get("result"));
                    myService.commodityToSpread(commodity);
                    return ResultJSONUtils.getJSONObjectBean(returnResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
    /**
     * (我的广告)商家id 获取 广告列表
     * @param appUser
     * @param
     * @return
     */
    @RequestMapping("/getCommodityInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getCommodityInfo(@CurrentUser AppUser appUser,HttpServletRequest request){
        log.info("商家id 获取 广告列表-------------->>/myController/getCommodityInfo");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        List<Advertisement> commodityInfoList = myService.getCommodityInfo(appUser.getId(), "");
        if (StringUtils.isEmpty(commodityInfoList)){
            returnResult.setMessage("暂无广告申请！！");
        }else {
            returnResult.setMessage("返回成功！！");
        }
        returnResult.setResult(commodityInfoList);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取爆款信息
     * @param authorId
     * @return
     */
    @RequestMapping(value = "/getHotSaleCommodity")
    @ResponseBody
    public JSONObject getHotSaleCommodity(String authorId,HttpServletRequest request){
        log.info("获取爆款信息-------------->>/myController/getHotSaleCommodity");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        List<Advertisement> commodityInfoList = myService.getCommodityInfo("", authorId);
        if (StringUtils.isEmpty(commodityInfoList)){
            returnResult.setMessage("暂无代言商品！！");
        }
        returnResult.setResult(commodityInfoList);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取广告费用信息
     * @param
     * @return
     */
    @RequestMapping(value = "/getAdvertisementFeeInfo")
    @ResponseBody
    public JSONObject getAdvertisementFeeInfo(String priceId,HttpServletRequest request){
        log.info("获取广告费用信息-------------->>/myController/getAdvertisementFeeInfo");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        List<AdPrice> advertisementFeeInfo = myService.getAdvertisementFeeInfo(priceId);
        System.out.println(advertisementFeeInfo);

        returnResult.setResult(advertisementFeeInfo);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 提现记录
     * @param
     * @return
     */
    @RequestMapping(value = "/getOutMoneyList")
    @ResponseBody
    @LoginRequired
    public JSONObject getOutMoneyList(@CurrentUser AppUser appUser,HttpServletRequest request){
        log.info("提现记录-------------->>/myController/getOutMoneyList");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        List<OutMoney> list = payService.getOutMoneyList(appUser.getId());
        if (CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无提现记录！");
        }
        returnResult.setResult(list);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 获取礼物列表
     * @param
     * @return
     */
    @RequestMapping(value = "/getGiftList")
    @ResponseBody
    public JSONObject getGiftList() {
        log.info("获取礼物列表 ============>>>>getGiftList");
        ReturnResult returnResult =new ReturnResult();
        List<Gift> list = payService.getGiftList();
        if (CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无礼物记录！");
        }
        returnResult.setResult(list);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 送礼物
     */
    @RequestMapping(value = "/sendMoney")
    @ResponseBody
    @LoginRequired
    public JSONObject sendMoney(@CurrentUser AppUser appUser, HttpServletRequest request) {
        log.info("送礼物 ============>>>>sendMoney");
        ReturnResult returnResult = new ReturnResult();
        Map<String, String> mapValue = getParameterMap(request);
        if(StringUtils.isEmpty(mapValue.get("giftId"))){
            returnResult.setMessage("缺少送给礼物ID！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else if(StringUtils.isEmpty(mapValue.get("sourceId"))){
            returnResult.setMessage("缺少送给用户ID！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Gift gift = payService.getGift(mapValue.get("giftId"));
        AppUser user = loginService.getAppUserMsg("","",mapValue.get("sourceId"));
        if(StringUtils.isNull(user)){
            returnResult.setMessage("您想送礼的用户，不存在！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if (appUser.getTotal().compareTo(gift.getGiftValue()) == -1) {
            returnResult.setCode("02");
            returnResult.setMessage("您的金额不足，请去充值！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        payService.sendMoney(appUser.getId(),gift.getGiftValue());
        BigDecimal bigDecimal = gift.getGiftValue().multiply(new BigDecimal(0.6)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payService.addIncome(user.getId(), bigDecimal);
//        消费者记录
        Order order = new Order();
        order.setOrderNo("YYXF" + RandomSaltUtil.randomNumber(14));
        order.setStatus("10B");
        order.setMobile(appUser.getPhone());
        order.setMerchantId(appUser.getId());
        order.setSourceId(user.getId());
        order.setMoney(gift.getGiftValue());
        order.setNote("送礼物");
        order.setTradeType("XF");
        payController.createOrder(order);
//        收益记录
        ChangeMoney shouMoney = new ChangeMoney();
        shouMoney.setChangeNo("YYSY" + RandomSaltUtil.randomNumber(14));
        shouMoney.setStatus("10B");
        shouMoney.setMobile(user.getPhone());
        shouMoney.setMerchantId(user.getId());
        shouMoney.setSourceId(appUser.getId());
        shouMoney.setMoney(bigDecimal);
        shouMoney.setNote("收益");
        shouMoney.setTradeType("SY");
        payController.createShouMoney(shouMoney);

        String s = appUser.getTotal().subtract(gift.getGiftValue()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        returnResult.setMessage(s);
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 扫一扫,二维码解析
     * @param appUser
     * @param request
     * @return
     */
    @RequestMapping(value = "/analysisCode")
    @ResponseBody
    @LoginRequired
    public JSONObject analysisCode(@CurrentUser AppUser appUser, HttpServletRequest request) {
        log.info("扫一扫,二维码解析 --------------->>/myController/analysisCode");
        ReturnResult returnResult = new ReturnResult();
        Map<String, String> parameterMap = getParameterMap(request);
        String siteId = parameterMap.get("siteId");


        if ("10A".equals(appUser.getUserStatus())){
            returnResult.setMessage("未实名,请前往实名验证！！");
            returnResult.setCode("01");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else if ("10B".equals(appUser.getUserStatus())){
            if (StringUtils.isEmpty(siteId)){
                returnResult.setMessage("扫描二维码，获取信息存在空值！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
            YuyueSite site = homePageService.getSite(siteId);
            YuyueSitePerson sitePerson = homePageService.getSitePerson(appUser.getId(), siteId);
            String startTime = site.getStartTime().split(" ")[1];
            String startHour=startTime.split(":")[0];
            String format = new SimpleDateFormat("HH").format(new Date());
            int personSum = Integer.parseInt(site.getPersonSum());
            int personTotal = Integer.parseInt(site.getPersonTotal());
            if (StringUtils.isNull(site)){
                returnResult.setMessage("查无本场次演出！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }else if (format.compareTo(startHour)>2){
                returnResult.setMessage("本场次结束进场！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }else if("10B".equals(sitePerson.getStatus())){
                returnResult.setMessage("已扫码进场！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            } else if(personSum >= personTotal ){
                returnResult.setMessage("本场次已经满员！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }else {
                homePageService.updateSite(siteId);
                YuyueSitePerson yuyueSitePerson=new YuyueSitePerson();
                String id=UUID.randomUUID().toString().replace("-","").toUpperCase();
                yuyueSitePerson.setId(id);
                yuyueSitePerson.setSiteId(siteId);
                yuyueSitePerson.setUserRealName(appUser.getRealName());
                yuyueSitePerson.setUserId(appUser.getId());
                homePageService.addSitePerson(yuyueSitePerson);
                returnResult.setMessage("验证成功，允许进场！！");
                returnResult.setStatus(Boolean.TRUE);
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }

        }else if ("10C".equals(appUser.getUserStatus())){
            returnResult.setMessage("实名中，敬请等候！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            returnResult.setMessage("实名失败,重新实名！！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

    }
}
