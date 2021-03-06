package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.*;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.utils.RandomSaltUtil;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 我的页面
 */

@Slf4j
@RestController
@RequestMapping(value = "/myController" ,produces = "application/json; charset=UTF-8")
public class MyController extends BaseController{

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
    public JSONObject feedback(HttpServletRequest request, HttpServletResponse response){
        log.info("意见反馈提交-------------->>/myController/feedback");
        Map<String, String> mapValue = getParameterMap(request, response);
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
            Feedback feed = myService.getFeedback(feedback.getDetails(),feedback.getContact());
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
    public JSONObject getMoneyList(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response) {
        log.info("充值记录和送礼记录-------------->>/myController/getMoneyList");
        ReturnResult returnResult=new ReturnResult();
        Map<String, String> parameterMap = getParameterMap(request, response);
        String page = parameterMap.get("page");
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        List<Order> list = myService.getMoneyList(user.getId(),begin,limit);
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
     * PC端支付通知
     * @return
     */
    @RequestMapping("/getMoneyStatus")
    @ResponseBody
    public JSONObject getMoneyStatus(HttpServletRequest request, HttpServletResponse response) {
        log.info("PC端支付通知-------------->>/myController/getMoneyStatus");
        ReturnResult returnResult=new ReturnResult();
        Map<String, String> parameterMap = getParameterMap(request, response);
        String moneyStatus = myService.getMoneyStatus(parameterMap.get("orderId"));
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(moneyStatus);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 打赏、提现、退款记录
     * @return
     */
    @RequestMapping("/changeMoneyList")
    @ResponseBody
    public JSONObject changeMoneyList(HttpServletRequest request, HttpServletResponse response) {
        log.info("打赏记录-------------->>/myController/changeMoneyList");
        ReturnResult returnResult=new ReturnResult();
        Map<String, String> parameterMap = getParameterMap(request, response);

        String token = request.getHeader("token");
        String userId = "";
        if(StringUtils.isNotEmpty(token) && StringUtils.isEmpty(parameterMap.get("videoId"))){
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
        }

        String page = parameterMap.get("page");
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        List<ChangeMoneyVo> list = myService.changeMoneyList(userId,parameterMap.get("videoId"),parameterMap.get("tradeType"),begin,limit);
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无记录！");
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
    public JSONObject addAdvertisementInfo(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        log.info("广告推广申请-------------->>/myController/addAdvertisementInfo");
        Map<String, String> parameterMap = getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        try {
            Advertisement advertisementInfo = myService.getAdvertisementInfo(user.getId());
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
            advertisement.setUserId(user.getId());
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

            Advertisement adver = myService.findAdvertisement(advertisement.getAgencyCode(),advertisement.getProduceAddr(),advertisement.getPhone());
            if(StringUtils.isNotNull(adver)){
                returnResult.setMessage("请勿重复添加！");
            } else {
                myService.addAdvertisemenInfo(advertisement);
                if ("1".equals(user.getUserType())) loginService.updateUserType(user.getId(),"3");
                else if ("2".equals(user.getUserType())) loginService.updateUserType(user.getId(),"4");
                else loginService.updateUserType(user.getId(),"3");
                returnResult.setMessage("信息插入成功");
                returnResult.setStatus(Boolean.TRUE);
            }
        }catch (Exception e){
            log.info("信息插入失败");
            returnResult.setMessage("信息插入失败");
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
    public JSONObject getAdvertisementInfo(@CurrentUser AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        log.info("查看广告推广申请信息-------------->>/myController/getAdvertisementInfo");
        getParameterMap(request, response);
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
     * @param page
     * @return
     */
    @RequestMapping("/myComments")
    @ResponseBody
    @LoginRequired
    public JSONObject getAllCommentByUserId(@CurrentUser AppUser appUser,String page,
                                            HttpServletRequest request, HttpServletResponse response){
        log.info("我的评论-------------->>/myController/myComments");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        List<UserCommentVo> allComment = userCommentService.getAllComment("", appUser.getId(),begin,limit);
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
    public JSONObject insertShowName(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        log.info("演出申请-------------->>/myController/insertShowName");
        Map<String, String> mapValue = getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        ShowName showInfo = myService.getShowInfo(user.getId());
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
        if (StringUtils.isEmpty(mapValue.get("teamName")) ){
            returnResult.setMessage("姓名或团队名称参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("size"))){
            returnResult.setMessage("人数参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("address"))){
            returnResult.setMessage("现住地址参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("categoryId"))){
            returnResult.setMessage("分类ID参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("description"))){
            returnResult.setMessage("节目名称参数为空！！");
        }else if (StringUtils.isEmpty(mapValue.get("phone"))){
            returnResult.setMessage("手机参数为空！！");
        }else if(StringUtils.isEmpty(mapValue.get("videoAddress"))){
            returnResult.setMessage("视频地址参数为空！！");
        }else {
            ShowName showName = new ShowName();
            System.out.println(mapValue.get("imageAddress"));
              if(StringUtils.isEmpty(mapValue.get("imageAddress")) ){
                  showName.setImageAddress("http://101.37.252.177:82/videoImage/videoImage.jpg");
              }else {
                  //    视频图片地址
                  showName.setImageAddress(mapValue.get("imageAddress"));
              }

            showName.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
            showName.setUserId(user.getId());
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
            //    视频地址
            showName.setVideoAddress(mapValue.get("videoAddress"));
            //    邮箱
            showName.setMail(mapValue.get("mail"));
            //    微信
            showName.setWeChat(mapValue.get("weChat"));

            System.out.println(showName);
            ShowName show = myService.findShowName(showName.getDescription(),showName.getPhone(),showName.getTeamName());
            if(StringUtils.isNotNull(show)){
                returnResult.setMessage("请勿重复添加！");
            } else {
                myService.insertShowName(showName.getId(),showName.getUserId(),showName.getTeamName(), showName.getSize(),
                        showName.getAddress(),showName.getCategoryId(),
                        showName.getDescription(),showName.getPhone(),
                        showName.getVideoAddress(), showName.getImageAddress(),showName.getMail(),showName.getWeChat());
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
    public JSONObject getShowInfo(@CurrentUser AppUser appUser,
                                  HttpServletRequest request, HttpServletResponse response){
        log.info("查看演出申请信息-------------->>/myController/getShowInfo");
        getParameterMap(request, response);
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
    public JSONObject myRelease(@CurrentUser AppUser appUser,HttpServletRequest request,String page
                                    , HttpServletResponse response){
        log.info("我的发布-------------->>/myController/myRelease");
        getParameterMap(request, response);
        ReturnResult returnResult =new ReturnResult();
        Map<String,Object> map = Maps.newHashMap();
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthorId(appUser.getId(),begin,limit);
        if (StringUtils.isEmpty(videoByAuthorId)){
            returnResult.setMessage("暂无发布视频");
            returnResult.setResult(videoByAuthorId);
        } else {
            map.put("Author", appUser);
            map.put("videoList", videoByAuthorId);
            returnResult.setMessage("返回成功！！");
            returnResult.setResult(map);
        }
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
    /**
     *商家上传 商品信息并支付
     * @param commodity
     */
    @RequestMapping("/commodityToSpread")
    @ResponseBody
    @LoginRequired
    public JSONObject commodityToSpread (Commodity commodity,String tradeType,@CurrentUser AppUser user,
                                         HttpServletRequest request, HttpServletResponse response){
        log.info("商家上传广告申请-------------->>/myController/commodityToSpread");
        getParameterMap(request, response);
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
            List<AdPrice> advertisementFeeInfo = myService.getAdvertisementFeeInfo(commodity.getPriceId(),"Y");
            if (StringUtils.isEmpty(advertisementFeeInfo)){
                returnResult.setMessage("价格id传入错误！！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
            commodity.setMerchantId(user.getId());
            AdPrice adPrice = advertisementFeeInfo.get(0);
            BigDecimal bigDecimal = new BigDecimal(adPrice.getAdTotalPrice()).multiply(new BigDecimal(adPrice.getAdDiscount()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            Order order = new Order();
            order.setTradeType(tradeType);
            order.setMoney(bigDecimal);
            //传入商品id重新支付
            if(StringUtils.isNotEmpty(commodity.getCommodityId())){
                List<Commodity> commodityInfoList = myService.getCommodityInfo("", "",commodity.getCommodityId(),-1,-1);
                if (StringUtils.isEmpty(commodityInfoList)){
                    returnResult.setMessage("未查询该商品申请！！");
                    returnResult.setResult(commodityInfoList);
                    returnResult.setStatus(Boolean.TRUE);
                    return ResultJSONUtils.getJSONObjectBean(returnResult);
                }else {
                    Order getOrder = payService.getOrderId(commodityInfoList.get(0).getOrderId());
                    if (StringUtils.isNull(getOrder)){
                        returnResult.setMessage("订单Id为空！！");
                        return ResultJSONUtils.getJSONObjectBean(returnResult);
                    }else if("10B".equals(getOrder.getStatus()) && "10B".equals(commodity.getStatus()) ){
                        returnResult.setMessage("该商品的订单已成功生成！！");
                        return ResultJSONUtils.getJSONObjectBean(returnResult);
                    }else if("10B".equals(getOrder.getStatus()) && "10C".equals(commodity.getStatus()) ){
                        returnResult.setMessage("该商品的订单已成功生成,并正在发布状态！！");
                        return ResultJSONUtils.getJSONObjectBean(returnResult);
                    }else if("10A".equals(getOrder.getStatus())){
                        if ("GGWX".equals(order.getTradeType())) {
                            try {
                                return payController.payWX(getOrder);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if ("GGZFB".equals(order.getTradeType())) {
                            try {
                                return payController.payZFB(getOrder);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    else if ("10D".equals(commodityInfoList.get(0).getStatus()) || "10D".equals(getOrder.getStatus())){
                        try {
                            jsonObject = payController.payYuYue(order, user);
                            //生成订单
                            if ("true".equals(jsonObject.getString("status"))){
                                String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                                if (StringUtils.isEmpty(orderId)){
                                    returnResult.setMessage("订单Id为空！！");
                                    return ResultJSONUtils.getJSONObjectBean(returnResult);
                                }
                                commodity.setOrderId(orderId);
                                Order newOrder = payService.getOrderId(orderId);
                                if ("10B".equals(newOrder.getStatus()))
                                    commodity.setStatus(newOrder.getStatus());
                                else
                                    commodity.setStatus("10A");
                                returnResult.setMessage("订单重新生成，等待审核！！");
                                returnResult.setStatus(Boolean.TRUE);
                                returnResult.setResult(jsonObject.get("result"));
                                myService.commodityToSpread(commodity);
                                return ResultJSONUtils.getJSONObjectBean(returnResult);
                            }else {
                                returnResult.setMessage("订单生成失败！！");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            //新的商品推广申请
            else{
                commodity.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());
                try {
                    jsonObject = payController.payYuYue(order, user);
                    //生成订单
                    if ("true".equals(jsonObject.getString("status"))){
                        String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                        if (StringUtils.isEmpty(orderId)){
                            returnResult.setMessage("订单Id为空！！");
                            return ResultJSONUtils.getJSONObjectBean(returnResult);
                        }
                        commodity.setOrderId(orderId);
                        Order getOrder = payService.getOrderId(orderId);
                        if ("10B".equals(getOrder.getStatus()))
                            commodity.setStatus(getOrder.getStatus());
                        else
                            commodity.setStatus("10A");
                        returnResult.setMessage("订单生成，等待审核！！");
                        returnResult.setStatus(Boolean.TRUE);
                        returnResult.setResult(jsonObject.get("result"));
                        myService.commodityToSpread(commodity);
                        return ResultJSONUtils.getJSONObjectBean(returnResult);
                    }else {
                        returnResult.setMessage("订单生成失败！！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }
    /**
     * 我的广告
     * 商家id获取广告列表;获取爆款信息;
     * @param appUser
     * @param
     * @return
     */
  /*  @RequestMapping("/getCommodityInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getCommodityInfo(@CurrentUser AppUser appUser,HttpServletRequest request){
        log.info("商家id 获取 广告列表-------------->>/myController/getCommodityInfo");
        getParameterMap(request);
        ReturnResult returnResult =new ReturnResult();
        String merchantId =request.getParameter("videoId");
        String videoId = request.getParameter("videoId");
        String commodityId = request.getParameter("commodityId");

    }*/

    /**
     * 我的广告;爆款;获取商品信息
     * 获取商人发布广告信息列表;获取爆款信息;获取单个商品信息
     * @param
     * @return
     */
    @RequestMapping(value = "/getHotSaleCommodityInfo")
    @ResponseBody
    public JSONObject getHotSaleCommodityInfo(HttpServletRequest request, HttpServletResponse response){
        log.info("获取爆款信息-------------->>/myController/getHotSaleCommodity");
        getParameterMap(request, response);
        String token = request.getHeader("token");
        String videoId = request.getParameter("videoId");
        String commodityId = request.getParameter("commodityId");
        String page = request.getParameter("page");
        String userId="";
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        if(StringUtils.isNotEmpty(token)) {
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
        }
        ReturnResult returnResult =new ReturnResult();
        //获取商品信息
        if(StringUtils.isNotEmpty(commodityId) ){
            List<Commodity> commodityInfoList = myService.getCommodityInfo("", "",commodityId,-1,-1);
            /*if (commodityInfoList.get(0).getOrderId())*/
            if (StringUtils.isEmpty(commodityInfoList)){
                returnResult.setMessage("未查询该商品！！");
            }
            returnResult.setResult(commodityInfoList);
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        //获取爆款
        else if (StringUtils.isNotEmpty(videoId) ){
            List<Commodity> commodityInfoList = myService.getCommodityInfo("", videoId,"", -1, -1);
            if (StringUtils.isEmpty(commodityInfoList)){
                returnResult.setMessage("暂无代言商品！！");
            }
            returnResult.setResult(commodityInfoList);
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            //我的广告
            List<Commodity> commodityInfoList = myService.getCommodityInfo(userId, "","", begin, limit);
            if (StringUtils.isEmpty(commodityInfoList)){
                returnResult.setMessage("暂无广告申请！！");
            }else {
                for (Commodity commodity:commodityInfoList) {
                    if ("10A".equals(commodity.getStatus())){
                        Order orderById=payService.getOrderId(commodity.getOrderId());
                        if (StringUtils.isNull(orderById)){
                            returnResult.setMessage("订单id为空！！");
                            returnResult.setResult(new java.awt.List());
                            return ResultJSONUtils.getJSONObjectBean(returnResult);
                        }
                        if ("10B".equals(orderById.getStatus())){
                            myService.updateCommodityStatus(commodity.getCommodityId(),"10B");
                        }else {
                            continue;
                        }
                    }else if ("10B".equals(commodity.getStatus()) &&  StringUtils.isNotEmpty(commodity.getStartDate()) &&   StringUtils.isNotEmpty(commodity.getEndDate())){
                        Date startDate = null;
                        Date endDate = null;
                        try {
                            startDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(commodity.getStartDate());
                            endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(commodity.getEndDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        boolean isStart = new Date().after(startDate);
                        boolean notEnd = new Date().before(endDate);

                        if (isStart &&  notEnd) myService.updateCommodityStatus(commodity.getCommodityId(),"10C");
                        else continue;
                    }else if ("10C".equals(commodity.getStatus()) &&  StringUtils.isNotEmpty(commodity.getStartDate()) &&   StringUtils.isNotEmpty(commodity.getEndDate())){
                        Date startDate = null;
                        Date endDate = null;
                        try {
                            startDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(commodity.getStartDate());
                            endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(commodity.getEndDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        boolean isStart = new Date().after(startDate);
                        boolean notEnd = new Date().before(endDate);

                        if (notEnd ==false) myService.updateCommodityStatus(commodity.getCommodityId(),"10D");
                        else continue;
                    }

                }
                commodityInfoList = myService.getCommodityInfo(userId, "","", begin, limit);
                returnResult.setMessage("返回成功！！");
            }
            returnResult.setResult(commodityInfoList);
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }


    }

    /**
     * 获取广告费用信息
     * @param
     * @return
     */
    @RequestMapping(value = "/getAdvertisementFeeInfo")
    @ResponseBody
    @LoginRequired
    public JSONObject getAdvertisementFeeInfo(@CurrentUser AppUser appUser,String priceId,HttpServletRequest request, HttpServletResponse response){
        log.info("获取广告费用信息-------------->>/myController/getAdvertisementFeeInfo");
        getParameterMap(request, response);
        ReturnResult returnResult =new ReturnResult();
        List<Order> ggOrder = payService.getGGOrder(appUser.getId(), "10B");
        List<AdPrice> advertisementFeeInfo = null ;
        if (StringUtils.isEmpty(ggOrder)){
            advertisementFeeInfo = myService.getAdvertisementFeeInfo(priceId,"Y");
        }else {
            advertisementFeeInfo = myService.getAdvertisementFeeInfo(priceId,"N");
        }
//        System.out.println(advertisementFeeInfo);

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
    public JSONObject getOutMoneyList(@CurrentUser AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        log.info("提现记录-------------->>/myController/getOutMoneyList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        String page = parameterMap.get("page");
        if (StringUtils.isEmpty(page))  page = "1";
        int limit = 10;
        int begin = (Integer.parseInt(page) - 1) * limit;
        ReturnResult returnResult =new ReturnResult();
        List<OutMoney> list = payService.getOutMoneyList(appUser.getId(),begin,limit);
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
     * giftId 礼物id
     * sourceId  收礼物的人
     */
    @RequestMapping(value = "/sendMoney")
    @ResponseBody
    @LoginRequired
    public JSONObject sendMoney(@CurrentUser AppUser appUser, HttpServletRequest request, HttpServletResponse response){
        log.info("送礼物 ============>>>>sendMoney");
        ReturnResult returnResult = new ReturnResult();
        Map<String, String> mapValue = getParameterMap(request, response);
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
        BigDecimal subtract = ResultJSONUtils.updateUserMoney(appUser.getTotal(),gift.getGiftValue(),"");
        payService.updateTotal(appUser.getId(),subtract);
        BigDecimal bigDecimal = gift.getGiftValue().multiply(new BigDecimal(0.6)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal add = ResultJSONUtils.updateUserMoney(user.getIncome(), bigDecimal, "+");
        payService.updateOutIncome(user.getId(), add);
//        消费者记录
        Order order = new Order();
        order.setOrderNo("YYXF" + RandomSaltUtil.randomNumber(14));
        order.setStatus("10B");
        order.setMobile(appUser.getPhone());
        order.setMerchantId(appUser.getId());
        order.setSourceId(user.getId());
        order.setMoney(gift.getGiftValue());
        order.setNote(gift.getRemark());
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
        shouMoney.setNote(gift.getRemark());
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
    public JSONObject analysisCode(@CurrentUser AppUser appUser, HttpServletRequest request, HttpServletResponse response){
        log.info("扫一扫,二维码解析 --------------->>/myController/analysisCode");
        ReturnResult returnResult = new ReturnResult();
        Map<String, String> parameterMap = getParameterMap(request, response);
        String siteId = parameterMap.get("siteId");


        if ("10A".equals(appUser.getUserStatus())){
            returnResult.setMessage("未实名,请前往实名验证！！");
            returnResult.setCode("01");
        }else if ("10B".equals(appUser.getUserStatus())){
            if (StringUtils.isEmpty(siteId)){
                returnResult.setMessage("扫描二维码，获取信息存在空值！！");
            }
            YuyueSite site = homePageService.getSite(siteId);
            if (StringUtils.isNull(site)){
                returnResult.setMessage("查无本场次演出！！");
            }
            YuyueSitePerson sitePerson = homePageService.getSitePerson(appUser.getId(), siteId);
            //未入场
            if(StringUtils.isNull(sitePerson)){
                String startTime = site.getAdmissionTime();
                String endTime = site.getEndTime();
                int personSum = Integer.parseInt(site.getPersonSum());
                int personTotal = Integer.parseInt(site.getPersonTotal());
                Date startDate = null;
                Date endDate = null;
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
                    try {
                        startDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startTime);
                        endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        log.info("时间格式错误！！");
                    }
                }
                if(personSum >= personTotal ){
                    returnResult.setMessage("本场次已经满员！！");
                }else if(new Date().before(startDate)){
                    returnResult.setMessage("本场次未开场！！");
                }else if(new Date().after(endDate)){
                    returnResult.setMessage("本场次结束进场！！");
                }else {
                    YuyueSitePerson yuyueSitePerson=new YuyueSitePerson();
                    String id=UUID.randomUUID().toString().replace("-","").toUpperCase();
                    yuyueSitePerson.setId(id);
                    yuyueSitePerson.setSiteId(siteId);
                    yuyueSitePerson.setUserRealName(appUser.getRealName());
                    yuyueSitePerson.setUserId(appUser.getId());
                    homePageService.addSitePerson(yuyueSitePerson);
                    //更新现场入场人数
                    homePageService.updateSite(siteId);
                    returnResult.setMessage("通过！！");
                    returnResult.setStatus(Boolean.TRUE);
                }
            }
            //已入场
            else if("10B".equals(sitePerson.getStatus())){
                returnResult.setMessage("通过！！");
                returnResult.setStatus(Boolean.TRUE);
            }
        }else if ("10C".equals(appUser.getUserStatus())){
            returnResult.setMessage("实名中，敬请等候！！");
        }else {
            returnResult.setMessage("实名失败,重新实名！！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 我的推广
     * @return
     */
    @RequestMapping("/getExtension")
    @ResponseBody
    public JSONObject getExtension(String phone,HttpServletRequest request, HttpServletResponse response) {
        log.info("我的推广-------------->>/myController/getExtension");
        ReturnResult returnResult=new ReturnResult();
        getParameterMap(request, response);
        String token = request.getHeader("token");
        String userId = "";
        List<AppUser> list = Lists.newArrayList();
        //手机端请求
        if(StringUtils.isNotEmpty(token)){
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
            AppUser user = loginService.getAppUserMsg("","",userId);
            list = loginService.getAppUserByFatherPhone(user.getPhone());
        }
        //后台web请求
        if (StringUtils.isNotEmpty(phone)) {
            list = loginService.getAppUserByFatherPhone(phone);
        }

        HashMap<String, Object> hashMap = Maps.newHashMap();
        int sum = 0;//总数
        int sum2 = 0;//艺人
        int sum3 = 0;//商户
        int sum4 = 0;//已实名的
        if(CollectionUtils.isNotEmpty(list)){
            sum = list.size();
            for (AppUser appUser:list) {
                if ("2".equals(appUser.getUserType())) {
                    sum2 += 1;
                } else if ("3".equals(appUser.getUserType())) {
                    sum3 += 1;
                }
                if ("10B".equals(appUser.getUserStatus())) {
                    sum4 += 1;
                }
            }
        }
        hashMap.put("sum",sum);
        hashMap.put("artist",sum2);
        hashMap.put("business",sum3);
        hashMap.put("collaborator",sum4);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(hashMap);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 申请为推广员
     * @return
     */
    @RequestMapping("/askExtension")
    @ResponseBody
    @LoginRequired
    public JSONObject askExtension(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response) {
        log.info("申请为推广员-------------->>/myController/askExtension");
        ReturnResult returnResult=new ReturnResult();
        getParameterMap(request, response);
        if(!"1".equals(user.getUserType())) {
            returnResult.setMessage("您好！只有普通用户可以申请为推广员！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }

        loginService.updateUserType(user.getId(),"6");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setMessage("成功申请为推广员！");
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
}
