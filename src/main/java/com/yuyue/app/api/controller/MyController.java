package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    /**
     * 意见反馈提交
     * @param request
     * @return
     */
    @RequestMapping("/feedback")
    @ResponseBody
    @LoginRequired
    public JSONObject addBarrages(@CurrentUser AppUser user, HttpServletRequest request){
        Map<String, String> mapValue = getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        if(StringUtils.isEmpty(mapValue.get("contact")) || StringUtils.isEmpty(mapValue.get("pictureUrl"))
                || StringUtils.isEmpty(mapValue.get("details")) ){
            returnResult.setMessage("参数为空！");
        } else {
            Feedback feedback = new Feedback();
            feedback.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
            feedback.setContact(mapValue.get("contact"));
            feedback.setPictureUrl(mapValue.get("pictureUrl"));
            feedback.setDetails(mapValue.get("details"));
            feedback.setUserId(user.getId());
            myService.insertFeedback(feedback);
            returnResult.setMessage("反馈成功！");
            returnResult.setStatus(Boolean.TRUE);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 充值记录和送礼记录 1111
     * @return
     */
    @RequestMapping("/getMoneyList")
    @ResponseBody
    @LoginRequired
    public JSONObject getMoneyList(@CurrentUser AppUser user){
        List<Order> list = myService.getMoneyList(user.getId());
        ReturnResult returnResult=new ReturnResult();
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
     * 广告推广申请
     * @param request
     * @return
     */

    @RequestMapping("/addAdvertisementInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject addAdvertisementInfo(@CurrentUser AppUser appUser, HttpServletRequest request){
        Map<String, String> parameterMap = getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        Advertisement advertisementInfo = myService.getAdvertisementInfo(appUser.getId());
        if( StringUtils.isNotNull(advertisementInfo) ){
            if("10B".equals(advertisementInfo.getStatus())){
                returnResult.setMessage("审核通过！！");
                returnResult.setStatus(Boolean.TRUE);
            }else {
                returnResult.setMessage("已提交，待审核");
            }
            returnResult.setResult(advertisementInfo);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        String userId=appUser.getId();
        String merchantAddr=parameterMap.get("merchantAddr");
        String businessLicense=parameterMap.get("businessLicense");
        String idCardZM=parameterMap.get("idCardZM");
        String idCardFM=parameterMap.get("idCardFM");
        String agencyCode=parameterMap.get("agencyCode");
        String merchantName=parameterMap.get("merchantName");
        String phone=parameterMap.get("phone");
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(merchantAddr) || StringUtils.isEmpty(businessLicense)  || StringUtils.isEmpty(idCardZM)
               ||StringUtils.isEmpty(idCardFM) || StringUtils.isEmpty(agencyCode) || StringUtils.isEmpty(merchantName) || StringUtils.isEmpty(phone) ){
            returnResult.setMessage("必填项存在空值");
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
        System.out.println(advertisement);
        myService.addAdvertisemenInfo(advertisement);
        returnResult.setMessage("信息插入成功");
        returnResult.setStatus(Boolean.TRUE);
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
    public JSONObject getAdvertisementInfo(@CurrentUser AppUser appUser){
        ReturnResult returnResult =new ReturnResult();
        Advertisement advertisementInfo = myService.getAdvertisementInfo(appUser.getId());
        if (StringUtils.isNull(advertisementInfo)){
            returnResult.setResult(new Object());
        }else
            returnResult.setResult(advertisementInfo);
        returnResult.setMessage("信息返回成功");
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
    public JSONObject getAllCommentByUserId(@CurrentUser AppUser appUser,String videoId){
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
    @LoginRequired
    public JSONObject insertShowName(@CurrentUser AppUser user, HttpServletRequest request){
        Map<String, String> mapValue = getParameterMap(request);
        ReturnResult returnResult=new ReturnResult();
        ShowName showInfo = myService.getShowInfo(user.getId());
        if (StringUtils.isNotNull(showInfo)){
            if("10B".equals(showInfo.getStatus())){
                returnResult.setMessage("审核通过！！");
                returnResult.setStatus(Boolean.TRUE);
            }else {
                returnResult.setMessage("已提交，待审核");

            }
            returnResult.setResult(showInfo);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if(StringUtils.isEmpty(mapValue.get("teamName")) || StringUtils.isEmpty(mapValue.get("size"))
                || StringUtils.isEmpty(mapValue.get("address")) || StringUtils.isEmpty(mapValue.get("cardZUrl"))
                || StringUtils.isEmpty(mapValue.get("cardFUrl")) || StringUtils.isEmpty(mapValue.get("categoryId"))
                || StringUtils.isEmpty(mapValue.get("description")) || StringUtils.isEmpty(mapValue.get("phone"))
                || StringUtils.isEmpty(mapValue.get("videoAddress"))){
            returnResult.setMessage("参数不可以为空！");
        } else {
            ShowName showName = new ShowName();
            showName.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
            showName.setUserId(user.getId());
            showName.setTeamName(mapValue.get("teamName"));
            showName.setSize(mapValue.get("size"));
            showName.setAddress(mapValue.get("address"));
            showName.setCardZUrl(mapValue.get("cardZUrl"));
            showName.setCardFUrl(mapValue.get("cardFUrl"));
            showName.setCategoryId(mapValue.get("categoryId"));
            showName.setDescription(mapValue.get("description"));
            showName.setPhone(mapValue.get("phone"));
            showName.setVideoAddress(mapValue.get("videoAddress"));
            showName.setMail(mapValue.get("mail"));
            showName.setWeChat(mapValue.get("weChat"));
            myService.insertShowName(showName);
            returnResult.setMessage("添加成功！");
            returnResult.setStatus(Boolean.TRUE);
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
    public JSONObject getShowInfo(@CurrentUser AppUser appUser){
        ReturnResult returnResult =new ReturnResult();
        ShowName showInfo = myService.getShowInfo(appUser.getId());
        if (StringUtils.isNull(showInfo)){
            returnResult.setResult(new Object());
        }else
            returnResult.setResult(showInfo);
        returnResult.setMessage("信息返回成功");
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
    public JSONObject myRelease(@CurrentUser AppUser appUser){
        ReturnResult returnResult =new ReturnResult();
        Map<String,Object> map = Maps.newHashMap();
        List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthorId(appUser.getId());
        if (StringUtils.isEmpty(videoByAuthorId)){
            returnResult.setMessage("暂无发布视频");
        }else
            map.put("Author",appUser);
            map.put("videoList",videoByAuthorId);
            returnResult.setMessage("返回成功！！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(map);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    /**
     *商家上传广告申请
     * @param commodity
     */
    @RequestMapping("/commodityToSpread")
    @ResponseBody
    @LoginRequired
    public JSONObject commodityToSpread (Commodity commodity,String tradeType,@CurrentUser AppUser user){
        ReturnResult returnResult =new ReturnResult();
        JSONObject jsonObject = null;
        if ( StringUtils.isEmpty(commodity.getCategory())
                ||StringUtils.isEmpty(commodity.getCommodityName())
                ||StringUtils.isEmpty(commodity.getAdWord())
                ||StringUtils.isEmpty(commodity.getAdUrl())
                ||StringUtils.isEmpty(commodity.getPayUrl())
                ||StringUtils.isEmpty(commodity.getPriceId())
                ||StringUtils.isEmpty(commodity.getAddr())
                ||StringUtils.isEmpty(tradeType)
                ||StringUtils.isEmpty(user.getId())){
            returnResult.setMessage("上传的9个参数均不可为空！！");
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
     * 商家id 获取 广告列表
     * @param appUser
     * @param
     * @return
     */
    @RequestMapping("/getCommodityInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getCommodityInfo(@CurrentUser AppUser appUser) {
        ReturnResult returnResult =new ReturnResult();
        List<Advertisement> commodityInfoList = myService.getCommodityInfo(appUser.getId(), "");
        if (StringUtils.isEmpty(commodityInfoList)){
            returnResult.setMessage("暂无广告申请");
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
    public JSONObject getHotSaleCommodity(String authorId){
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
    public JSONObject getAdvertisementFeeInfo(String priceId){
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
    public JSONObject getOutMoneyList(@CurrentUser AppUser appUser){
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
     * 送礼物
     */
    @RequestMapping(value = "/sendMoney")
    @ResponseBody
    @LoginRequired
    public JSONObject sendMoney(@CurrentUser AppUser appUser, HttpServletRequest request) {
        ReturnResult returnResult = new ReturnResult();
        Map<String, String> mapValue = getParameterMap(request);
        if(StringUtils.isEmpty(mapValue.get("sourceId"))){
            returnResult.setMessage("缺少送给用户ID！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        AppUser user = loginService.getAppUserMsg("","",mapValue.get("sourceId"));
        if(StringUtils.isNull(user)){
            returnResult.setMessage("您想送礼的用户，不存在！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if (StringUtils.isEmpty(mapValue.get("money"))
                || new BigDecimal(mapValue.get("money")).compareTo(BigDecimal.ZERO) == 0
                || appUser.getTotal().compareTo(new BigDecimal(mapValue.get("money"))) == -1) {
            returnResult.setCode("02");
            returnResult.setMessage("您的金额不足，请去充值！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        BigDecimal money = new BigDecimal(mapValue.get("money"));
        payService.sendMoney(appUser.getId(),money);
        payService.updateTotal(user.getId(), new BigDecimal(mapValue.get("money"))
                .multiply(new BigDecimal(0.6)).setScale(2, BigDecimal.ROUND_HALF_UP));
        Order order = new Order();
        order.setOrderNo("YYXF" + RandomSaltUtil.randomNumber(14));
        order.setStatus("10B");
        order.setStatusCode("100001");
        order.setMobile(appUser.getPhone());
        order.setMerchantId(appUser.getId());
        order.setSourceId(user.getId());
        order.setMoney(new BigDecimal(mapValue.get("money")));
        order.setNote("送礼物");
        order.setTradeType("XF");
        payController.createOrder(order);
        returnResult.setMessage(appUser.getNickName()+"送"+user.getNickName()+" "+mapValue.get("money")+"和元币！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
}
