package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.auth0.jwt.JWT;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.MallShopService;
import com.yuyue.app.api.service.MyService;
import com.yuyue.app.api.service.PayService;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.enums.Variables;
import com.yuyue.app.utils.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(value = "/mallShop" , produces = "application/json; charset=UTF-8")
public class MallShopController extends BaseController{

    private static  final  java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
    //public static Map<String,BigDecimal> addMoneyToMerchantMap = new HashMap<>();


    @Autowired
    private MallShopService mallShopService;
    @Autowired
    private MyService myService;
    @Autowired
    private PayService payService;
    @Autowired
    private PayController payController;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 查询我的关注商铺列表
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "isAttention")
    @ResponseBody
    @LoginRequired
    public ReturnResult isAttention(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询我的关注商铺列表-------------->>/mallShop/isAttention");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");

        List<ShopAttention> shopAttentions = mallShopService.getShopAttentions(user.getId(), shopId);
        if (StringUtils.isEmpty(shopAttentions)){
            returnResult.setMessage("未关注！");
            returnResult.setResult(new Object());
        }else {
            returnResult.setResult(shopAttentions.get(0));
            returnResult.setMessage("返回成功！");
        }
        returnResult.setStatus(Boolean.TRUE);

        return returnResult;
    }


    /**
     * 查询我关注的商铺列表
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getShopAttentions")
    @ResponseBody
    @LoginRequired
    public ReturnResult getShopAttentions(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询我的关注商铺列表-------------->>/mallShop/getShopAttentions");
        getParameterMap(request, response);
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";
        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        List<ShopAttention> shopAttentions = mallShopService.getShopAttentions(user.getId(), "");
        PageInfo<ShopAttention> pageInfo=new PageInfo<>(shopAttentions);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        List<MallShop> mallShops =  new ArrayList<>();
        if(StringUtils.isNotEmpty(shopAttentions)){
            for (ShopAttention shopAttention:shopAttentions
                 ) {
                MallShop myMallShop = mallShopService.getMyMallShop(shopAttention.getShopId());
                if (StringUtils.isNull(myMallShop)){
                    continue;
                }
                AppUser appUserMsg = loginService.getAppUserMsg("", "", myMallShop.getMerchantId());
                myMallShop.setHeadUrl(appUserMsg.getHeadpUrl());
                mallShops.add(myMallShop);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("mallShops",mallShops);
        map.put("pages",pages);
        map.put("currentPage",currentPage);
        map.put("total",total);
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(map);

        return returnResult;
    }

    /**
     * 添加商铺关注
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addShopAttention")
    @ResponseBody
    @LoginRequired
    public ReturnResult addShopAttention(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("添加商铺关注-------------->>/mallShop/addShopAttention");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");
        List<ShopAttention> shopAttentions = mallShopService.getShopAttentions(user.getId(), shopId);
        if (StringUtils.isNotEmpty(shopAttentions)){
            returnResult.setMessage("已关注！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(new Object());
            return returnResult;
        }
        String id = UUID.randomUUID().toString().replace("-","").toUpperCase();
        ShopAttention shopAttention = new ShopAttention();

        shopAttention.setId(id);
        shopAttention.setUserId(user.getId());
        shopAttention.setShopId(shopId);
        mallShopService.addShopAttention(shopAttention);
        returnResult.setMessage("关注成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(new Object());
        return returnResult;
    }


    @RequestMapping(value = "cancelShopAttention")
    @ResponseBody
    @LoginRequired
    public ReturnResult cancelShopAttention(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询我的商铺-------------->>/mallShop/cancelShopAttention");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");
        mallShopService.cancelShopAttention(user.getId(),shopId);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(new Object());
        returnResult.setMessage("删除成功！");
        return returnResult;
    }



    /**
     * 查询我的商铺
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMyMallShop")
    @ResponseBody
    public ReturnResult getMyMallShop(HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询我的商铺-------------->>/mallShop/getMyMallShop");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            returnResult.setMessage("商铺id为空");
            return returnResult;
        }
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        if (StringUtils.isNull(myMallShop)){
            returnResult.setMessage("未查询到该商铺");
            return returnResult;
        }
        List<Specification> specification = mallShopService.getSpecification(shopId);
        if (StringUtils.isEmpty(specification)){
            specification = new ArrayList<>();
        }
        myMallShop.setSpecifications(specification);
        myMallShop.setDistanceValue(myMallShop.getDistances().getDistanceValue()+"公里");
        String token = request.getHeader("token");
        String userId="";
        if(StringUtils.isNotEmpty(token)) {
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
            List<ShopAttention> shopAttentions = mallShopService.getShopAttentions(userId, shopId);
            if (StringUtils.isNotEmpty(shopAttentions)){
                myMallShop.setIsAttention(true);
            }else {
                myMallShop.setIsAttention(false);
            }
        }else {
            myMallShop.setIsAttention(false);
        }
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(myMallShop);
        return returnResult;
    }


    /**
     * 查询我的商铺（我的广告 ）
     * @param
     * @param response
     * @return
     */
    @RequestMapping(value = "getMyMallShops")
    @ResponseBody
    @LoginRequired
    public ReturnResult getMyMallShops(@CurrentUser AppUser user,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询我的商铺（我的广告）-------------->>/mallShop/getMyMallShops");
        getParameterMap(request, response);
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";
        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        List<MallShop> myMallShops = mallShopService.getMyMallShops(user.getId());
        if (StringUtils.isEmpty(myMallShops)){
            returnResult.setMessage("未查询到商铺");
            return returnResult;
        }

        for (MallShop myShop:myMallShops
             ) {
            //规格
            myShop.setImages(mallShopService.getShopImage(myShop.getShopId()));
            myShop.setAdPrice(myService.getAdvertisementFeeInfo(myShop.getPriceId(),"").get(0));
            List<Specification> specification = mallShopService.getAllSpecification(myShop.getShopId());
            myShop.setSpecifications(specification);
        }
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(myMallShops);
        return returnResult;
    }

    /**
     * 查询所有符合条件的商铺
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getAllMallShop")
    @ResponseBody
    public ReturnResult getAllMallShop(HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询所有符合条件的商铺-------------->>/mallShop/getAllMallShop");
        getParameterMap(request, response);
        String myArea = request.getParameter("myArea");//区域
        String distanceId = request.getParameter("distanceId");//服务距离
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        String content = request.getParameter("content");//分类、名称、详情
        String gdLon = request.getParameter("gdLon");//经度
        String gdLat = request.getParameter("gdLat");//纬度
        String sortType = request.getParameter("sortType");//排序类别
        if (StringUtils.isEmpty(gdLon) || StringUtils.isEmpty(gdLat)){
            returnResult.setMessage("经纬度不可以为空！");
            return returnResult;
        }
        if (StringUtils.isEmpty(sortType)){
            sortType = "distance";
        }
        if (StringUtils.isEmpty(distanceId)){
            distanceId = "1";
        }
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";

        Integer pageNum = Integer.valueOf(page);
        Integer pageSum = Integer.valueOf(pageSize);
        JSONObject jsonObject = new JSONObject();
//        if (redisUtil.existsKey("getAllMallShop") && "1".equals(page) && StringUtils.isEmpty(sortType)) {
//            jsonObject =JSON.parseObject((String)redisUtil.getString("getAllMallShop"));
//            log.info("------redis缓存中取出数据-------");
//        } else {

        //计算距离太久，可以优化
//        JSONObject lonLarByAddress = GouldUtils.getLonLarByAddress(gdLon, gdLat);
//        lonLarByAddress.get("");
//        HttpUtils.doPost(Variables.sendStockJPushUrl,sb.toString());

        List<MallShop> allMallShop = mallShopService.getAllMallShop(myArea,content);
        List<MallShopVo> list = GouldUtils.getNearbyStoreByDistinceAsc(sortType, new BigDecimal(gdLon), new BigDecimal(gdLat), allMallShop);
//            获取距离
        List<Distance> allDistance = mallShopService.getDistanceAll(distanceId);
        String allDistanceValue = allDistance.get(0).getDistanceValue();
        Iterator<MallShopVo> iter = list.iterator();
        Long along = 0L;
        if (!allDistanceValue.contains("全部")){
            along = Long.valueOf(allDistanceValue) * 1000L;
        }
        while (iter.hasNext()) {
            MallShopVo mallShopVo = (MallShopVo) iter.next();
//              双向选择
            String distanceValue = mallShopVo.getDistances().getDistanceValue();
            if (StringUtils.isNotEmpty(distanceValue) && !distanceValue.contains("全部")) {
                if (mallShopVo.getDistance() > Long.valueOf(distanceValue) * 1000L || along > Long.valueOf(distanceValue) * 1000L) {
                    iter.remove();
                } else if (!allDistanceValue.contains("全部") && mallShopVo.getDistance() > along){
                    iter.remove();
                }
            } else if (!allDistanceValue.contains("全部") && mallShopVo.getDistance() > along){
                iter.remove();
            }
        }

        jsonObject.put("total",list.size());
        jsonObject.put("pageNum",pageNum);
        jsonObject.put("pageSize",pageSum);
        jsonObject.put("pages", (list.size()+ pageSum-1) / pageSum);
        // 构建分割
        List<MallShopVo> batchSubList = Lists.newArrayList();
        PageUtil<MallShopVo> batchListSplitIterator = new PageUtil<>(list, pageNum,pageSum);
        // 迭代每一批数据
        while (batchListSplitIterator.hasNext()){
            log.info("position====>>>"+batchListSplitIterator.position());
            log.info("page====>>>"+page);
            batchSubList = batchListSplitIterator.next();
            if (batchListSplitIterator.position() == (Long.valueOf(pageNum * pageSum))) {
                break;
            }
        }
//        if ("1".equals(page) && StringUtils.isEmpty(sortType)) {
//            redisUtil.setString("getAllMallShop", jsonObject.toJSONString(),600);
//            log.info("------redis存入数据-------");
//        }
        if (Integer.valueOf(page) > Integer.valueOf(jsonObject.getString("pages"))) {
            jsonObject.put("list",new ArrayList<>());
        } else {
            jsonObject.put("list",batchSubList);
        }
        returnResult.setResult(jsonObject);
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }

    /**
     * 查询商城搜索距离的
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getDistanceAll")
    @ResponseBody
    public ReturnResult getDistanceAll(HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询商城搜索距离的-------------->>/mallShop/getDistanceAll");
        getParameterMap(request, response);
        JSONArray jsonArray = new JSONArray();
        if (redisUtil.existsKey("getDistanceAll")) {
            jsonArray =JSON.parseArray((String)redisUtil.getString("getDistanceAll"));
            log.info("------redis缓存中取出数据-------");
        } else {
            List<Distance> allDistance = mallShopService.getDistanceAll("");
            for (Distance distance:allDistance) {
                if (!distance.getDistanceValue().contains("全部")) {
                    distance.setDistanceValue(distance.getDistanceValue()+"km");
                }
            }
            jsonArray=JSON.parseArray(JSONObject.toJSONString(allDistance));
            redisUtil.setString("getDistanceAll", jsonArray.toJSONString(),600);
            log.info("------redis存入数据-------");
        }
        returnResult.setResult(jsonArray);
        returnResult.setMessage("获取距离成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


    /**
     * 获取爆款
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMallShopByVideo")
    @ResponseBody
    public ReturnResult getMallShopByVideo(HttpServletRequest request, HttpServletResponse response){
        log.info("获取爆款------------->>/mallShop/getMallShopByVideo");
        getParameterMap(request, response);
        ReturnResult returnResult = new ReturnResult();
        String videoId = request.getParameter("videoId");
        if (StringUtils.isEmpty(videoId)){
            returnResult.setMessage("视频id不可为空！");
            return returnResult;
        }
        List<MallShop> mallShopByVideoId = mallShopService.getMallShopByVideoId(videoId);
        returnResult.setMessage("返回成功！");
        returnResult.setResult(mallShopByVideoId);
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


    /**
     * 添加商铺
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "insertMyMallShop")
    @ResponseBody
    @LoginRequired
    public ReturnResult insertMyMallShop(@CurrentUser AppUser user, HttpServletRequest request, HttpServletResponse response) {
        ReturnResult returnResult = new ReturnResult();
        log.info("添加商铺-------------->>/mallShop/insertMyMallShop");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");
        String sourcePay = request.getParameter("sourcePay");
        String priceId = request.getParameter("priceId");
        String tradeType = request.getParameter("tradeType");
        String distanceId = request.getParameter("distanceId");
        if (StringUtils.isEmpty(shopId)){
            returnResult.setMessage("商铺id为空");
            return returnResult;
        }
        /*---------------------------------生成订单-------------------------------*/

        if ("GGZFB".equals(tradeType) || "GGWX".equals(tradeType)){

        }else{
            returnResult.setMessage("交易类型错误！");
            return returnResult;
        }
        List<AdPrice> advertisementFeeInfo =  myService.getAdvertisementFeeInfo(priceId,"");

        if (StringUtils.isEmpty(advertisementFeeInfo)){
            returnResult.setMessage("价格id传入错误！！");
            return returnResult;
        }
        AdPrice adPrice = advertisementFeeInfo.get(0);
        //计算支付金额
        BigDecimal bigDecimal = new BigDecimal(adPrice.getAdTotalPrice()).multiply(new BigDecimal(adPrice.getAdDiscount()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        Order order = new Order();
        order.setTradeType(tradeType);
        order.setMoney(bigDecimal);
        //传入商品id重新支付
        JSONObject jsonObject = null;
        /*---------------------------------生成订单结束-------------------------------*/
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        //商铺已存在情况下重新支付，---------> 未支付状态或是 支付超时状态
        if (StringUtils.isNotNull(myMallShop) && ("10B".equals(myMallShop.getStatus()) || "10C".equals(myMallShop.getStatus())
                || "10D".equals(myMallShop.getStatus())   || "10E".equals(myMallShop.getStatus()))){
                //商铺已存在   已支付、已发布、已过期状态(再次支付)
                if ("10B".equals(myMallShop.getStatus())){
                    returnResult.setMessage("已添加,待审核！");
                    returnResult.setStatus(Boolean.TRUE);
                }else if ("10C".equals(myMallShop.getStatus())){
                    returnResult.setMessage("该订单正在发布！");
                    returnResult.setStatus(Boolean.TRUE);
                }else if ("10D".equals(myMallShop.getStatus())){
                    returnResult.setMessage("该商铺已经被停止发布！");
                    returnResult.setStatus(Boolean.TRUE);
                }else if ("10E".equals(myMallShop.getStatus())){
                    if(StringUtils.isNotEmpty(myMallShop.getOrderId())){
                        Order getOrder = payService.getOrderId(myMallShop.getOrderId());
                        if (StringUtils.isNull(getOrder)){
                            returnResult.setMessage("未查询该订单！！");
                            return returnResult;
                        }
                        //订单未支付状态     --->  去支付
                        else if("10A".equals(getOrder.getStatus())){
                            if ("GGWX".equals(order.getTradeType())) {
                                try {
                                    jsonObject = payController.payWX(getOrder);
                                    returnResult.setStatus(Boolean.TRUE);
                                    returnResult.setMessage("添加成功！");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if ("GGZFB".equals(order.getTradeType())) {
                                try {
                                    jsonObject = payController.payZFB(getOrder);
                                    // String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                                    returnResult.setStatus(Boolean.TRUE);
                                    //returnResult.setMessage(jsonObject.getString("result"));
                                    returnResult.setMessage("添加成功！");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                returnResult.setMessage("支付类型错误！");
                            }
                            return returnResult;
                        }
                        else if("10B".equals(getOrder.getStatus())  ){
                            //修改商铺状态
                            myMallShop.setStatus("10B");
                            mallShopService.updateMyMallShopInfo(myMallShop);
                            return returnResult;
                        }
                        //支付超时状态    支付失败   商铺到期   -->重新生成新的订单
                        else if ("10E".equals(myMallShop.getStatus())  ||
                                "10C".equals(getOrder.getStatus()) ||
                                "10D".equals(getOrder.getStatus())){

                            try {
                                String orderId = null;
                                //扫码支付
                                if (StringUtils.isNotEmpty(sourcePay) && "YYSM".equals(sourcePay)){
                                    log.info("扫码支付");
                                    jsonObject = payController.payNative(user,order, request, response);
                                    orderId = JSON.parseObject(jsonObject.getString("message")).toJSONString();
                                    returnResult.setMessage(orderId);
                                }
                                //app 支付宝微信支付
                                else {
                                    log.info("手机支付");
                                    jsonObject = payController.payYuYue(order, user);
                                    //成功生成新的订单，获取订单ID

                                }

                                //生成订单
                                if ("true".equals(jsonObject.getString("status"))){

                                    if (StringUtils.isEmpty(orderId)){
                                        returnResult.setMessage("订单Id为空！！");
                                        return returnResult;
                                    }else if (StringUtils.isNotEmpty(sourcePay) && "YYSM".equals(sourcePay)){
                                        orderId = JSON.parseObject(jsonObject.getString("message")).toJSONString();
                                        returnResult.setMessage(orderId);
                                    }else {
                                        orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                                    }
                                    myMallShop.setOrderId(orderId);
                                    myMallShop.setPriceId(adPrice.getPriceId());
                                    Order newOrder = payService.getOrderId(orderId);
                                    if ("10B".equals(newOrder.getStatus()))
                                        myMallShop.setStatus("10B");
                                    else
                                        myMallShop.setStatus("10A");
                                    returnResult.setResult(jsonObject.get("result"));
                                    returnResult.setMessage("订单重新生成，等待审核！！");
                                    returnResult.setStatus(Boolean.TRUE);
                                    returnResult.setResult(jsonObject.get("result"));
                                    mallShopService.updateMyMallShopInfo(myMallShop);
                                    return returnResult;
                                }else {
                                    returnResult.setMessage("订单生成失败！！");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    returnResult.setStatus(Boolean.TRUE);
                }
                return returnResult;
        }
        /*---------------------------------------------新的商铺申请--------------------------------------------*/
        else {

            //新的商铺申请
            if (StringUtils.isEmpty(request.getParameter("category"))){
                returnResult.setMessage("商品/服务分类不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("commodityName"))){
                returnResult.setMessage("商品/服务名称不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("fare"))){
                returnResult.setMessage("运费不能为空！");
                return returnResult;
            }
            else if (StringUtils.isEmpty(request.getParameter("commodityPrice"))){
                returnResult.setMessage("商铺价格不能为空！");
                return returnResult;
            }
            //验证金额格式
            java.util.regex.Matcher match=pattern.matcher(request.getParameter("fare").toString());
            java.util.regex.Matcher pMatch=pattern.matcher(request.getParameter("commodityPrice").toString());
            if(match.matches()==false) {
                returnResult.setMessage("运费格式输入错误！！！");
                return returnResult;
            }if(pMatch.matches()==false) {
                returnResult.setMessage("商铺价格输入错误！！！");
                return returnResult;
            }
//            else if (StringUtils.isEmpty(request.getParameter("businessTime"))){
//                returnResult.setMessage("营业时间不能为空！");
//                return returnResult;
//            }
            else if (StringUtils.isEmpty(request.getParameter("merchantAddr"))){
                returnResult.setMessage("商家地址不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("merchantPhone"))){
                returnResult.setMessage("商家电话不能为空！");
                return returnResult;
            }
            MallShop mallShop =new MallShop();
            if (StringUtils.isEmpty(distanceId)){
                mallShop.setDistanceId("1");
            }else {
                if(distanceId.matches("[1-8]")){
                    mallShop.setDistanceId(distanceId);
                }else {
                    returnResult.setMessage("距离id输入错误！");
                    return returnResult;
                }
            }
            mallShop.setShopId(shopId);
            mallShop.setMerchantId(user.getId());
            mallShop.setCategory(request.getParameter("category"));
            mallShop.setCommodityName(request.getParameter("commodityName"));
            //压缩图
            String commodityImages = request.getParameter("images");
            //原图
            String originalImages = request.getParameter("originalImages");
            if (StringUtils.isEmpty(originalImages)){
                returnResult.setMessage("原图路径不可为空！");
                return returnResult;
            }else {
                mallShopService.deleteImageByShopId(shopId);
                if (originalImages.contains(";")){
                    //原图
                    String[] originalImage = originalImages.split(";");
                    String[] commodityImage = null;
                    if (StringUtils.isNotEmpty(commodityImages) && commodityImages.contains(";")){
                        //压缩图
                        commodityImage = commodityImages.split(";");
                    }else {
                        commodityImage[0] = commodityImages;
                    }
                    for ( Byte i = 0 ; i < originalImage.length ; i++) {
                        ShopImage shopImage = new ShopImage();
                        String id = UUID.randomUUID().toString().replace("-","").toUpperCase();
                        shopImage.setId(id);
                        shopImage.setOriginalImage(originalImage[i]);
                        if (i < commodityImage.length){
                            shopImage.setImagePath(commodityImage[i]);
                            System.out.println("缩图："+commodityImage[i]);
                        }
                        shopImage.setImageSort(i);
                        shopImage.setShopId(shopId);
                        System.out.println("原图："+originalImage[i]);
                        System.out.println("----------");
                        mallShopService.insertShopImage(shopImage);
                    }
                }else {
                    ShopImage shopImage = new ShopImage();
                    String id = UUID.randomUUID().toString().replace("-","").toUpperCase();
                    shopImage.setId(id);
                    shopImage.setImagePath(commodityImages);
                    shopImage.setOriginalImage(originalImages);
                    shopImage.setImageSort((byte) 0);
                    shopImage.setShopId(shopId);
                    mallShopService.insertShopImage(shopImage);
                }
            }
            mallShop.setDetail(request.getParameter("detail"));

//            String specifications= request.getParameter("specifications");
//            if (StringUtils.isNotEmpty(specifications)){
//                String[] specificationsArray = specifications.split(";");
//                for (String specification:specificationsArray
//                ) {
//
//                }
//            }
            mallShop.setPriceId(priceId);
            mallShop.setServiceType(request.getParameter("serviceType"));
            mallShop.setFare(new BigDecimal(request.getParameter("fare")));
            mallShop.setCommodityPrice(new BigDecimal(request.getParameter("commodityPrice")));
            mallShop.setVideoPath(request.getParameter("remark"));
            mallShop.setBusinessTime(request.getParameter("businessTime"));
            mallShop.setBusinessStatus(request.getParameter("businessStatus"));
            mallShop.setMerchantAddr(request.getParameter("merchantAddr"));
            mallShop.setMerchantPhone(request.getParameter("merchantPhone"));
            mallShop.setServiceArea(request.getParameter("serviceArea"));
            mallShop.setFeeArea(request.getParameter("feeArea"));
            mallShop.setVideoPath(request.getParameter("videoPath"));
            mallShop.setRemark(request.getParameter("remark"));
            mallShop.setRemark("N");

            try {
                JSONObject lonLarByAddress = GouldUtils.getLonLarByAddress(mallShop.getMerchantAddr());
                if (com.yuyue.app.utils.StringUtils.isNotEmpty(lonLarByAddress)) {
                    JSONObject object = JSONObject.parseObject(JSONArray.parseArray(lonLarByAddress.getString("geocodes")).get(0).toString());
                    String[] locations = object.getString("location").split(",");
                    mallShop.setGdLat(new BigDecimal(locations[1]));
                    mallShop.setGdLon(new BigDecimal(locations[0]));
                }
            } catch (Exception e) {
                returnResult.setMessage("地址填写错误！");
                return returnResult;
            }
            mallShopService.insertMyMallShop(mallShop);
            /*----------------------------------------接支付------------------------------------------------*/
            //新的商品推广申请
            System.out.println(sourcePay);
               try {
                  jsonObject = payController.payYuYue(order, user);
                  //生成订单
                   String orderId = null;
                   if (StringUtils.isNotEmpty(sourcePay) && "YYSM".equals(sourcePay)){
                       log.info("扫码支付");
                       jsonObject = payController.payNative(user,order, request, response);
                       if (tradeType.contains("ZFB")){
                           orderId = jsonObject.getString("message");
                           returnResult.setMessage(orderId);
                       }else {
                           orderId = JSON.parseObject(jsonObject.getString("result")).getString("out_trade_no");
                           returnResult.setMessage("订单生成，等待审核！！");
                       }
                   }else {
                       log.info("手机支付");
                       jsonObject = payController.payYuYue(order, user);
                       //成功生成新的订单，获取订单ID
                       orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                       //returnResult.setMessage(orderId);
                       returnResult.setMessage("订单生成，等待审核！！");
                   }
                   if ("true".equals(jsonObject.getString("status"))){
                       //String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                       if (StringUtils.isEmpty(orderId)){
                           returnResult.setMessage("订单Id为空！！");
                           return returnResult;
                       }
                            mallShop.setOrderId(orderId);
                            mallShop.setStatus("10A");
                            returnResult.setStatus(Boolean.TRUE);
                            returnResult.setResult(jsonObject.get("result"));
                            mallShopService.insertMyMallShop(mallShop);
                            return returnResult;
                        }else {
                            returnResult.setMessage("订单生成失败！！");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        }
        return returnResult;
    }

    /**
     * 修改我的商铺
     * @param mallShop
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateMyMallShopInfo")
    @ResponseBody
    @LoginRequired
    public ReturnResult updateMyMallShopInfo(MallShop mallShop,String  imageStr,HttpServletRequest request, HttpServletResponse response){
        log.info("修改商铺信息------------->>/mallShop/updateMyMallShopInfo");
        getParameterMap(request, response);
        ReturnResult returnResult = new ReturnResult();
         if(StringUtils.isEmpty(mallShop.getShopId())){
            returnResult.setMessage("商铺ID不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallShop.getCategory())){
            returnResult.setMessage("商品/服务分类不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallShop.getCommodityName())){
            returnResult.setMessage("商品/服务名称不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallShop.getDetail())){
            returnResult.setMessage("商品/服务介绍不能为空！");
            return returnResult;
        }
        else if (StringUtils.isEmpty(mallShop.getFare().toString())){
            returnResult.setMessage("运费不能为空！");
            return returnResult;
        }
        //验证金额格式
       java.util.regex.Matcher match=pattern.matcher(mallShop.getFare().toString());
        if(match.matches()==false)
        {
            returnResult.setMessage("运费格式输入错误！！！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallShop.getBusinessTime())){
            returnResult.setMessage("营业时间不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallShop.getMerchantAddr())){
            returnResult.setMessage("商家地址不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallShop.getMerchantPhone())){
            returnResult.setMessage("商家电话不能为空！");
            return returnResult;
        }
        mallShop.setIsRevise("Y");
        if (StringUtils.isEmpty(mallShop.getDistanceId())){
            mallShop.setDistanceId("1");
        }else if(!mallShop.getDistanceId().matches("[1-8]")){
            returnResult.setMessage("距离id输入错误！");
            return returnResult;
        }
        try {
            JSONObject lonLarByAddress = GouldUtils.getLonLarByAddress(mallShop.getMerchantAddr());
            if (com.yuyue.app.utils.StringUtils.isNotEmpty(lonLarByAddress)) {
                JSONObject object = JSONObject.parseObject(JSONArray.parseArray(lonLarByAddress.getString("geocodes")).get(0).toString());
                String[] locations = object.getString("location").split(",");
                mallShop.setGdLat(new BigDecimal(locations[1]));
                mallShop.setGdLon(new BigDecimal(locations[0]));
            }
        } catch (Exception e) {
            returnResult.setMessage("地址填写错误！");
            return returnResult;
        }
        mallShopService.updateMyMallShopInfo(mallShop);
        //压缩图
        //String commodityImages = request.getParameter("images");
        String commodityImages = mallShop.getImageStr();
        System.out.println(commodityImages);
        //原图
        String originalImages = request.getParameter("originalImages");
        System.out.println(originalImages);
        String shopId = mallShop.getShopId();
        if (StringUtils.isEmpty(originalImages)){
            returnResult.setMessage("原图路径不可为空！");
            return returnResult;
        }else {
            mallShopService.deleteImageByShopId(shopId);
            if (originalImages.contains(";")) {
                //原图
                String[] originalImage = originalImages.split(";");
                String[] commodityImage = null;
                if (StringUtils.isNotEmpty(commodityImages) && commodityImages.contains(";")) {
                    //压缩图
                    commodityImage = commodityImages.split(";");
                } else {
                    commodityImage[0] = commodityImages;
                }
                for (Byte i = 0; i < originalImage.length; i++) {
                    ShopImage shopImage = new ShopImage();
                    String id = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                    shopImage.setId(id);
                    shopImage.setOriginalImage(originalImage[i]);
                    if (i < commodityImage.length) {
                        shopImage.setImagePath(commodityImage[i]);
                        System.out.println("缩图：" + commodityImage[i]);
                    }
                    shopImage.setImageSort(i);
                    shopImage.setShopId(shopId);
                    System.out.println("原图：" + originalImage[i]);
                    System.out.println("----------");
                    mallShopService.insertShopImage(shopImage);
                }
            } else {
                ShopImage shopImage = new ShopImage();
                String id = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                shopImage.setId(id);
                shopImage.setImagePath(commodityImages);
                shopImage.setOriginalImage(originalImages);
                shopImage.setImageSort((byte) 0);
                shopImage.setShopId(shopId);
                mallShopService.insertShopImage(shopImage);
            }
        }
//        if (StringUtils.isNotEmpty(imageStr)){
//            mallShopService.deleteImageByShopId(mallShop.getShopId());
//            if (imageStr.contains(";")){
//                String[] images = imageStr.split(";");
//                for ( Byte i = 0 ; i < images.length ; i++) {
//                    ShopImage shopImage = new ShopImage();
//                    shopImage.setImagePath(images[i]);
//                    shopImage.setImageSort(i);
//                    shopImage.setShopId(mallShop.getShopId());
//                    System.out.println(images[i]);
//                    mallShopService.insertShopImage(shopImage);
//                }
//            }else {
//                ShopImage shopImage = new ShopImage();
//                shopImage.setImagePath(imageStr);
//                shopImage.setImageSort((byte)0);
//                shopImage.setShopId(mallShop.getShopId());
//                mallShopService.insertShopImage(shopImage);
//            }
//
//        }


        returnResult.setMessage("修改成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


    /**
     * 修改我的商铺状态
     * @param mallShop
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateMyMallShopStatus")
    @ResponseBody
    @LoginRequired
    public ReturnResult updateMyMallShopStatus(MallShop mallShop,HttpServletRequest request, HttpServletResponse response){
        log.info("修改我的商铺状态------------->>/mallShop/updateMyMallShopStatus");
        getParameterMap(request, response);
        ReturnResult returnResult = new ReturnResult();
        String businessStatus = request.getParameter("businessStatus");
        String shopId = request.getParameter("shopId");
        if ("rest".equals(businessStatus) || "open".equals(businessStatus)){
            mallShopService.updateMyMallShopStatus(businessStatus,shopId);
            returnResult.setMessage("修改成功！");
        }else {
            returnResult.setMessage("状态错误！");
        }


        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }



    /**
     * 修改商铺图片(弃用)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "insertShopImage")
    @ResponseBody
    @LoginRequired
    public ReturnResult insertShopImage(HttpServletRequest request, HttpServletResponse response){
        log.info("删除商铺图片------------->>/mallShop/deleteShopImage");
        getParameterMap(request, response);
        ReturnResult returnResult = new ReturnResult();
        String commodityImage = request.getParameter("images");

        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            returnResult.setMessage("商铺id为空！");
            return returnResult;
        }
        if (StringUtils.isEmpty(commodityImage)){
            returnResult.setMessage("图片路径为空！");
            return returnResult;
        }else {
            String[] images = commodityImage.split(";");

            for ( Byte i = 0 ; i < images.length ; i++) {
                ShopImage shopImage = new ShopImage();
                shopImage.setImagePath(images[i]);
                shopImage.setImageSort(i);
                shopImage.setShopId(shopId);
                System.out.println(images[i]);
                mallShopService.insertShopImage(shopImage);
            }
            returnResult.setMessage("图片修改成功！");
            returnResult.setStatus(Boolean.TRUE);
            return returnResult;
        }
    }

    /**
     * 删除商铺图片
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteShopImage")
    @ResponseBody
    @LoginRequired
    public ReturnResult deleteShopImage(HttpServletRequest request, HttpServletResponse response){
        log.info("删除商铺图片------------->>/mallShop/deleteShopImage");
        getParameterMap(request, response);
        ReturnResult returnResult = new ReturnResult();
        String imagePath = request.getParameter("imagePath");
        if (StringUtils.isEmpty(imagePath)){
            returnResult.setMessage("图片路径为空！");
            return returnResult;
        }
        mallShopService.deleteShopImage(imagePath);
        returnResult.setMessage("图片删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


    /**
     * 通过商铺id -->查询商品规格
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getSpecification")
    @ResponseBody
    public ReturnResult getSpecification(HttpServletRequest request, HttpServletResponse response){
        log.info("通过商铺id查询规格------------->>/mallShop/getSpecification");
        getParameterMap(request, response);
        ReturnResult returnResult = new ReturnResult();
        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            returnResult.setMessage("商品id不能为空！！");
            return returnResult;
        }
        List<Specification> specification = mallShopService.getAllSpecification(shopId);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(specification);
        returnResult.setMessage("返回成功");
        return returnResult;

    }

    /**
     * 通过规格id  -->查询规格
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getSpecificationById")
    @ResponseBody
    public ReturnResult getSpecificationById(HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("通过规格id查询规格------------->>/mallShop/getSpecificationById");
        getParameterMap(request, response);
        String commodityId = request.getParameter("commodityId");
        if (StringUtils.isEmpty(commodityId)){
            returnResult.setMessage("规格id不能为空！！");
            return returnResult;
        }
        Specification specification = mallShopService.getSpecificationById(commodityId);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(specification);
        returnResult.setMessage("返回成功");
        return returnResult;

    }

    /**
     * 编辑规格(添加修改)
     * @param commodityId
     * @param shopId
     * @param commodityDetail
     * @param commoditySize
     * @param commodityPrice
     * @param commodityReserve
     * @param imagePath
     * @param status
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "editSpecification")
    @ResponseBody
    @LoginRequired
    public ReturnResult editSpecification(String commodityId,String shopId ,String commodityDetail,String commoditySize,
                                          String commodityPrice,String commodityReserve,
                                          String imagePath,String status,
                                          HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        Specification specification = new Specification();
        getParameterMap(request, response);
        log.info("编辑规格(添加修改)------------->>/mallShop/editSpecification");
        if (StringUtils.isEmpty(commodityPrice)){
            returnResult.setMessage("价格不能为空！");
            return returnResult;
        }
        // 判断小数点后2位的数字的正则表达式
        java.util.regex.Matcher match=pattern.matcher(commodityPrice);
        if(match.matches()==false)
        {
            returnResult.setMessage("价格输入错误！");
            return returnResult;
        }else {
            specification.setCommodityPrice(new BigDecimal(commodityPrice));
        }if (StringUtils.isEmpty(commodityReserve)){
            returnResult.setMessage("库存不能为空！");
            return returnResult;
        }else if(commodityReserve.matches("[0-9]+")){
            specification.setCommodityReserve(Integer.parseInt(commodityReserve));
        }else {
            returnResult.setMessage("库存不是整数类型！");
            return returnResult;
        }if ("10A".equals(status)  || "10B".equals(status)){
            specification.setStatus(status);
        }else {
            returnResult.setMessage("状态输入错误！");
            return returnResult;
        }
        specification.setCommodityDetail(commodityDetail);
        specification.setCommodityReserve(Integer.parseInt(commodityReserve));
        specification.setCommoditySize(commoditySize);
        specification.setImagePath(imagePath);
        //如果是添加规格
        if (StringUtils.isEmpty(commodityId)){
            if (StringUtils.isEmpty(shopId)){
                returnResult.setMessage("商铺id不能为空！");
                return returnResult;
            }
            specification.setShopId(shopId);
            specification.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());
            mallShopService.insertSpecification(specification);
            returnResult.setMessage("规格添加成功！");

        }else {
            //修改规格
            Specification specificationById = mallShopService.getSpecificationById(commodityId);
            if (StringUtils.isNotEmpty(status) &&(StringUtils.isEmpty(specificationById.getStatus())
                    ||"10A".equals(specificationById.getStatus()) || "10B".equals(specificationById.getStatus()))){
                specification.setStatus(status);
            }else if ("10D".equals(specificationById.getStatus()) && "10A".equals(status)){
                specification.setStatus("10A");
            }else if ("10D".equals(specificationById.getStatus()) && "10B".equals(status)){
                specification.setStatus("10B");
            }
            specification.setCommodityId(commodityId);
            mallShopService.updateSpecification(specification);
            returnResult.setMessage("规格修改成功！");
        }
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }



    /**
     * 添加商品规格
     * @param shopId
     * @param commodityDetail
     * @param commodityPrice
     * @param commodityReserve
     * @param imagePath
     * @param status
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "insertSpecification")
    @ResponseBody
    @LoginRequired
    public ReturnResult insertSpecification(String shopId ,String commodityDetail,String commoditySize,
                                            String commodityPrice,String commodityReserve,
                                            String imagePath,String status,
                                            @CurrentUser AppUser user,
                                            HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        Specification specification = new Specification();
        specification.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        // 判断小数点后2位的数字的正则表达式
       java.util.regex.Matcher match=pattern.matcher(commodityPrice);
        if(match.matches()==false)
        {
            returnResult.setMessage("价格输入错误！");
            return returnResult;
        }
        else {
            specification.setCommodityPrice(new BigDecimal(commodityPrice));
        }
        if(commodityReserve.matches("[0-9]+")){
            specification.setCommodityReserve(Integer.parseInt(commodityReserve));
        }else {
            returnResult.setMessage("库存不是整数类型！");
            return returnResult;
        }if ("10A".equals(status)  || "10B".equals(status)){
            specification.setStatus(status);
        }else {
            returnResult.setMessage("状态输入错误！");
            return returnResult;
        }
        specification.setShopId(shopId);
        specification.setCommodityDetail(commodityDetail);
        specification.setCommoditySize(commoditySize);
        specification.setImagePath(imagePath);
        mallShopService.insertSpecification(specification);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(specification);
        returnResult.setMessage("规格添加成功！");
        return returnResult;
    }

    /**
     * 删除规格
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteSpecificationById")
    @ResponseBody
    @LoginRequired
    public ReturnResult deleteSpecificationById(HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("删除规格------------->>/mallShop/deleteSpecificationById");
        getParameterMap(request, response);
        String commodityId = request.getParameter("commodityId");
        if (StringUtils.isEmpty(commodityId)){
            returnResult.setMessage("规格id不能为空！！");
            return returnResult;
        }
        mallShopService.deleteSpecification(commodityId);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setMessage("删除成功！");
        return returnResult;

    }

    /**
     * 修改规格
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateSpecification")
    @ResponseBody
    @LoginRequired
    public ReturnResult updateSpecification(Specification specification,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("修改规格------------->>/mallShop/updateSpecification");
        getParameterMap(request, response);
        String commodityId = request.getParameter("commodityId");
        if (StringUtils.isEmpty(commodityId)){
            returnResult.setMessage("规格id不能为空！");
            return returnResult;
        }
        mallShopService.updateSpecification(specification);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setMessage("修改成功！");
        return returnResult;

    }


    /**
     *查询我的购物车
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getCart")
    @ResponseBody
    @LoginRequired
    public ReturnResult getCart(@CurrentUser  AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("查询我的购物车------------->>/mallShop/getCart");

        getParameterMap(request, response);
        List<Cart> carts = mallShopService.getCarts("",appUser.getId());
        if (StringUtils.isEmpty(carts)){
            returnResult.setMessage("暂无商品！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(carts);
            return returnResult;
        }
        //购物车返回值List
        List<ResultCart> resultCarts = new ArrayList<>();
        for (Cart cart : carts
             ) {

            MallShop myMallShop = mallShopService.getMyMallShop(cart.getShopId());
            if (StringUtils.isNull(myMallShop)){
                continue;
            }
            if ("N".equals(myMallShop.getIsRevise()) && "10C".equals(myMallShop.getStatus())
                    && "open".equals(myMallShop.getBusinessStatus())){
                myMallShop.setBusinessStatus("open");
            }else {
                myMallShop.setBusinessStatus("rest");
            }
            //第一次
            if (StringUtils.isEmpty(resultCarts)) {
                Specification specification= mallShopService.getSpecificationById(cart.getCommodityId());

                if (StringUtils.isNull(specification)){
                    continue;
                }
                cart.setSpecification(specification);
                ResultCart resultCart = new ResultCart();
                resultCart.setShopId(myMallShop.getShopId());
                resultCart.setCommodityName(myMallShop.getCommodityName());
                resultCart.setBusinessStatus(myMallShop.getBusinessStatus());
                List<Cart> newCarts = new ArrayList<>();
                newCarts.add(cart);
                resultCart.setCommodityList(newCarts);
                resultCarts.add(resultCart);
                continue;
            }
            Boolean status = false;
            //第二次及多次
            for (ResultCart resultCart : resultCarts
            ) {
                //如果有已存在相同商铺的商品，仅将商品添加到List
                if (resultCart.getShopId().equals(myMallShop.getShopId())) {
                    Specification specification= mallShopService.getSpecificationById(cart.getCommodityId());
                    if (StringUtils.isNull(specification)){
                        continue;
                    }
                    cart.setSpecification(specification);
                    List<Cart> addCarts = resultCart.getCommodityList();
                    addCarts.add(cart);
                    resultCart.setCommodityList(addCarts);
                    status =true;
                    break;
                }
            }
            System.out.println(status);
            if (status == false) {
                Specification specification= mallShopService.getSpecificationById(cart.getCommodityId());
                if (StringUtils.isNull(specification)){
                    continue;
                }
                cart.setSpecification(specification);
                ResultCart resultCart1 = new ResultCart();
                resultCart1.setShopId(myMallShop.getShopId());
                resultCart1.setCommodityName(myMallShop.getCommodityName());
                resultCart1.setBusinessStatus(myMallShop.getBusinessStatus());
                List<Cart> newCarts = new ArrayList<>();
                newCarts.add(cart);
                resultCart1.setCommodityList(newCarts);
                resultCarts.add(resultCart1);
                continue;
            }
        }

        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(resultCarts);
        return returnResult;
    }

    /**
     * 添加修改购物车
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "editCart")
    @ResponseBody
    @LoginRequired
    public ReturnResult editCart(@CurrentUser  AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("添加修改购物车------------->>/mallShop/editCart");

        getParameterMap(request, response);
        Cart cart = new Cart();


        String commodityId = request.getParameter("commodityId");
        String shopId = request.getParameter("shopId");
        //String commodityName = request.getParameter("commodityName");
        //String commodityPrice = request.getParameter("commodityPrice");
        //String commoditySize = request.getParameter("commoditySize");
        String commodityNum = request.getParameter("commodityNum");


        cart.setConsumerId(appUser.getId());
        //java.util.regex.Matcher match=pattern.matcher(commodityPrice);
        if (StringUtils.isEmpty(commodityId)){
            returnResult.setMessage("规格id为空！");
            return returnResult;
        }else{
            cart.setCommodityId(commodityId);
        }
        if (StringUtils.isEmpty(shopId)){
            returnResult.setMessage("商铺id为空！");
            return returnResult;
        }else {
            cart.setShopId(shopId);
        }
        /*if (StringUtils.isEmpty(commodityName)){
            returnResult.setMessage("商品名为空！");
            return returnResult;
        }else {
            cart.setCommodityName(commodityName);
        }
        if(match.matches()==false)
        {
            returnResult.setMessage("价格输入错误！");
            return returnResult;
        }
        else {
            cart.setCommodityPrice(new BigDecimal(commodityPrice));
        }*/

        if(commodityNum.matches("[0-9]+")){
            String cartId = request.getParameter("cartId");
            int numAfter = Integer.parseInt(commodityNum);
            //判断是否加入过该商品
            if (StringUtils.isEmpty(cartId)){
                 Cart isAdd = mallShopService.getCart(commodityId, appUser.getId());
                 if (StringUtils.isNull(isAdd)){
                     cartId = UUID.randomUUID().toString().replace("-","").toUpperCase();
                 }else {
                     //曾添加过,修改商品数量
                     int numBefore= isAdd.getCommodityNum();
                     numAfter = numBefore + numAfter;
                     cartId = isAdd.getCartId();
                 }

            }else {
                //购物车id不为空，直接修改信息
                List<Cart> oneCart = mallShopService.getCarts(cartId, "");
                if (StringUtils.isEmpty(oneCart)){
                    returnResult.setMessage("购物车id错误！");
                    return returnResult;
                }
            }
            cart.setCommodityNum(numAfter);
            cart.setCartId(cartId);
        }else {
            returnResult.setMessage("数量不是整数类型！");
            return returnResult;
        }

        //cart.setCommoditySize(commoditySize);
        mallShopService.editCart(cart);
        returnResult.setMessage("编辑成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }

    /**
     * 删除购物车
     * @param
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteCart")
    @ResponseBody
    @LoginRequired
    public ReturnResult deleteCart(@CurrentUser  AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("修改规格------------->>/mallShop/deleteCart");
        getParameterMap(request, response);
        String cartId = request.getParameter("cartId");
        String shopId = request.getParameter("shopId");
        if (cartId.contains(";")){
            String[] split = cartId.split(";");
            for (String s : split
                 ) {
                mallShopService.deleteCart(s,shopId);
            }
        }mallShopService.deleteCart(cartId,shopId);


        returnResult.setMessage("删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }

    /**
     * 获取用户评价
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMallComments")
    @ResponseBody
    public ReturnResult getMallComments(HttpServletRequest request, HttpServletResponse response) {

         ReturnResult returnResult = new ReturnResult();
         log.info("获取用户评价------------->>/mallShop/getMallComments");
         getParameterMap(request, response);
         String shopId = request.getParameter("shopId");
         if (StringUtils.isEmpty(shopId)){
             returnResult.setMessage("shopId不能为空！");
             return returnResult;
         }
        String page = request.getParameter("page");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))  page = "1";
        PageHelper.startPage(Integer.parseInt(page), 10);
        List<MallComment> mallComments = mallShopService.getMallComments(shopId);
         if (StringUtils.isNotEmpty(mallComments)){
             for (MallComment mallComment : mallComments
                  ) {
                 AppUser appUserMsg = loginService.getAppUserMsg("", "", mallComment.getConsumerId());
                 AppUser appUser  =  new AppUser();
                 appUser.setNickName(appUserMsg.getNickName());
                 appUser.setHeadpUrl(appUserMsg.getHeadpUrl());
                 mallComment.setAppUser(appUser);

             }
         }
        PageInfo<MallComment> pageInfo=new PageInfo<>(mallComments);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        Map<String,Object> map = new HashMap<>();
        map.put("mallComments",mallComments);
        map.put("pages",pages);
        map.put("currentPage",currentPage);
        map.put("total",total);

        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(map);
        return returnResult;

    }


    /**
     * 添加用户评价
     * @param mallComment
     * @return
     */
    @RequestMapping(value = "addMallComment")
    @ResponseBody
    @LoginRequired
    public ReturnResult addMallComment(@CurrentUser  AppUser appUser,MallComment mallComment,
                                       HttpServletRequest request, HttpServletResponse response) {
        ReturnResult returnResult = new ReturnResult();
        log.info("添加用户评价------------->>/mallShop/addMallComment");
        getParameterMap(request, response);


        if (StringUtils.isEmpty(mallComment.getShopId())){
            returnResult.setMessage("shopId（商品）不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallComment.getOrderId())){
            returnResult.setMessage("订单id不可为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallComment.getCommoditySize())){
            returnResult.setMessage("commoditySize（商品规格）不能为空！");
            return returnResult;
        }else if (StringUtils.isEmpty(mallComment.getContent())){
            returnResult.setMessage("content（内容）不能为空！");
            return returnResult;
        }

        try {
             if (mallComment.getScore()<0 || mallComment.getScore()>5){
                returnResult.setMessage("评分错误！");
                return returnResult;
            }
        }catch (Exception e){
            log.info("分数类型错误");
            returnResult.setMessage("分数类型错误！");
            return returnResult;
        }
        MallComment isComment = mallShopService.getMallComment(mallComment.getShopId(),mallComment.getOrderId(), appUser.getId());
        if (StringUtils.isNotNull(isComment)){
            returnResult.setMessage("该商品已评价！");
            return returnResult;
        }

        mallComment.setCommentId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        mallComment.setConsumerId(appUser.getId());
        mallShopService.addMallComment(mallComment);
        //获取评论平均值
        double score = mallShopService.getScore(mallComment.getShopId());
        score = (double) Math.round(score * 10) / 10;
        MallShop myMallShop = mallShopService.getMyMallShop(mallComment.getShopId());
        myMallShop.setScore(score);
        mallShopService.updateMyMallShopInfo(myMallShop);
        returnResult.setMessage("评价成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }

    /**
     * 临时订单（老版）
     * @param cartStr
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "oldTemporaryOrder")
    @ResponseBody
    public ReturnResult oldTemporaryOrder(String cartStr,
                                       HttpServletRequest request, HttpServletResponse response) {
        ReturnResult returnResult = new ReturnResult();
        log.info("生成订单项------------->>/mallShop/temporaryOrder");
        getParameterMap(request, response);
        List<ResultCart> resultCarts = new ArrayList<>();
        ResultCart resultCart = new ResultCart();
        if (cartStr.contains("-")){
            String[] cartStrings = cartStr.split("-");
            for (String s:cartStrings
            ) {
                String[] split = s.split(":");
                resultCart = getReturnResult(split[0], split[1]);
                System.out.println(resultCart);
                if (StringUtils.isNull(resultCart)){
                    returnResult.setMessage("未查询到商铺或规格:"+resultCart.getShopId());
                    return returnResult;
                }
                //第一次插入
                if (StringUtils.isEmpty(resultCarts)){
                        resultCarts.add(resultCart);
                }
                else {
                    //第二次或第n次插入
                    Boolean status = false;
                    for (ResultCart r:resultCarts
                         ) {
                        //如果存在相同的值，直接插入

                        if (r.getShopId().equals(resultCart.getShopId())){
                            r.getCommodities().addAll(resultCart.getCommodities());
                            BigDecimal subtract = r.getPayAmount().add(resultCart.getPayAmount()).subtract(resultCart.getFare());
                            r.setPayAmount(subtract);
                            status = true;
                            continue;
                        }
                    }if (status == false){
                        System.out.println("-----------");
                        resultCarts.add(resultCart);
                    }
                }

            }
        }else {
            String[] split = cartStr.split(":");
            System.out.println(split[0]+ split[1]);
            resultCart = getReturnResult(split[0], split[1]);
            if (StringUtils.isNull(resultCart)){
                returnResult.setMessage("未查询到该商铺或规格");
                return returnResult;
            }
            else {
                resultCarts.add(resultCart);
            }
        }
        returnResult.setMessage("成功生成订单项！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(resultCarts);
        return returnResult;
    }


    /**
     * 获取购物车返回值
     * @param commodityId
     * @param commodityNum
     * @return
     */
    public ResultCart getReturnResult(String commodityId,String commodityNum){
        List<Specification> commodities= new ArrayList<>();
        ResultCart resultCart = new ResultCart();
        //获取规格id
        Specification specification = mallShopService.getSpecificationById(commodityId);
        if (StringUtils.isNull(specification)){
            return resultCart;
        }
        //获取商铺id
        String shopId = specification.getShopId();
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        if (StringUtils.isNull(myMallShop)){
            return resultCart;
        }
        //获取商铺运费
        BigDecimal fare = myMallShop.getFare();
        //获取商品价格
        BigDecimal commodityPrice = specification.getCommodityPrice();
        BigDecimal payAmount = commodityPrice.multiply(new BigDecimal(commodityNum)).add(fare);
        resultCart.setCommodityName(myMallShop.getCommodityName());
        resultCart.setShopId(shopId);
        resultCart.setFare(myMallShop.getFare());
        resultCart.setPayAmount(payAmount);
        commodities.add(specification);
        resultCart.setCommodities(commodities);
        return resultCart;
//        if (StringUtils.isNull(resultCart)){
//                resultCart.setCommodityName(myMallShop.getCommodityName());
//                resultCart.setShopId(shopId);
//                resultCart.setPayAmount(payAmount);
//                commodities.add(specification);
//                resultCart.setCommodities(commodities);
//
//
//        }else {
//            if (resultCart.getShopId().equals(shopId)){
//                commodities.add(specification);
//                resultCart.setCommodities(commodities);
//            }else {
//                ResultCart resultCart =new  ResultCart();
//                resultCart.setCommodityName(myMallShop.getCommodityName());
//                resultCart.setShopId(shopId);
//                resultCart.setPayAmount(payAmount);
//                commodities.add(specification);
//                resultCart.setCommodities(commodities);
//                returnResult.setMessage("返回成功！");
//                returnResult.setStatus(Boolean.TRUE);
//                returnResult.setResult(resultCart);
//                return returnResult;
//
//
//            }
//
//        }
//        returnResult.setMessage("返回成功！");
//        returnResult.setStatus(Boolean.TRUE);
//        returnResult.setResult(resultCart);
//        return returnResult;
    }


    /**
     * 生成订单项(老版)
     * @param
     * @return
     */
    @RequestMapping(value = "oldCreateOrder")
    @ResponseBody
    @LoginRequired
    public ReturnResult oldCreateOrder(@CurrentUser  AppUser appUser,String cartStr,String addressId,
                                       String payType,
                                       HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("生成订单项------------->>/mallShop/createOrder");
        getParameterMap(request, response);
        OrderItem orderItem = null;
        MallAddress mallAddress = null;
        if (StringUtils.isEmpty(addressId)){
            mallAddress = mallShopService.getMallAddress(addressId);
            if (StringUtils.isNull(mallAddress)){
                returnResult.setMessage("地址不能为空！");
                return  returnResult;
            }
        }
        //      地址id
        if (StringUtils.isEmpty(addressId)){
            returnResult.setMessage("收货地址不可为空");
            return  returnResult;
        }if ("SCZFB".equals(payType) || "SCWX".equals(payType)){

        }else {
            returnResult.setMessage("支付方式错误！");
            return  returnResult;
        }
        //判断库存问题
        Map<String, String> stringStringMap = null;
        try {
            stringStringMap = MallUtils.splitCartString(cartStr);
            if(StringUtils.isEmpty(stringStringMap)){
                returnResult.setMessage("cartStr不可为空！");
                return  returnResult;
            }
            for (String key:stringStringMap.keySet()
            ) {
                Specification specificationById = mallShopService.getSpecificationById(key);
                if (StringUtils.isNull(specificationById)){
                    continue;
                }
                try {
                    if(specificationById.getCommodityReserve() >= Integer.parseInt(stringStringMap.get(key))){
                        continue;
                    }else {
                        returnResult.setMessage(specificationById.getCommodityDetail()+"库存不足！");
                        return  returnResult;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    returnResult.setMessage("cartStr格式中商品数量格式错误！");
                    return  returnResult;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setMessage("cartStr格式错误！");
            return  returnResult;
        }

        List<ResultCart> getOrder = (List<ResultCart>) oldTemporaryOrder(cartStr,request,response).getResult();
        if (StringUtils.isEmpty(getOrder)){
            returnResult.setMessage("生成订单异常");
            return  returnResult;
        }
        //获取交易总额
        //BigDecimal payTotal = new BigDecimal(0);
        String payTotal = "0";

        for (ResultCart resultCart: getOrder) {
            System.out.println("单个商铺"+resultCart.getPayAmount());
            payTotal = new BigDecimal(payTotal).add(resultCart.getPayAmount()).toString();

        }
        System.out.println("总金额"+new BigDecimal(payTotal));
        Order order =new Order();
        order.setMoney(new BigDecimal(payTotal));
        order.setTradeType(payType);
        JSONObject jsonObject = null;
        try {
            jsonObject = payController.payYuYue(order, appUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String orderId = null ;

        if ("true".equals(jsonObject.getString("status"))){
            orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
            returnResult.setResult(jsonObject.get("result"));
        }
        Order getPayStatus = payService.getOrderId(orderId);

        //生成订单项
        for (String specificationId:stringStringMap.keySet()
        ) {
            orderItem = addOrderItem(specificationId, Integer.parseInt(stringStringMap.get(specificationId)),mallAddress);
            //设置地址id
            orderItem.setAddressId(addressId);
            //消费者id
            orderItem.setConsumerId(appUser.getId());
            //订单id
            orderItem.setOrderId(orderId);
            //支付状态
            orderItem.setStatus(getPayStatus.getStatus());
            //设置运费
            mallShopService.editMallOrderItem(orderItem);
        }

        returnResult.setMessage("订单生成成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;

    }


    /**
     * 第二版 修改订单状态
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateOrderItemsStatus")
    @ResponseBody
    @LoginRequired
    public ReturnResult updateOrderItemsStatus(@CurrentUser  AppUser appUser,
                                               HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("第二版 修改订单状态------------->>/mallShop/updateOrderItemsStatus");
        getParameterMap(request, response);
        String orderItemId = request.getParameter("orderItemId");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(orderItemId)){
            returnResult.setMessage("订单id为空！");
            return returnResult;
        }if (StringUtils.isEmpty(status)){
            returnResult.setMessage("状态为空！");
            return returnResult;
        }else {
            if ("10B".equals(status)  ||  "10C".equals(status) || "10D".equals(status) || "10E".equals(status)){

                OrderItemVo orderItemVo = mallShopService.getMallOrderItemById(orderItemId);
                if (StringUtils.isNull(orderItemVo)){
                    returnResult.setMessage("未查询该订单！");
                    return returnResult;
                }else {
                    mallShopService.updateOrderItemsStatus(orderItemVo.getOrderItemId(),status);
                    returnResult.setMessage("修改成功！");
                    returnResult.setStatus(Boolean.TRUE);
                    return returnResult;
                }
            }else {
                returnResult.setMessage("订单状态错误！");
                return returnResult;
            }

        }
    }


    /**
     * 修改订单状态
     * @param appUser
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value = "updateOrderItemsStatus")
//    @ResponseBody
//    @LoginRequired
//    public ReturnResult updateOrderItemsStatus(@CurrentUser  AppUser appUser,
//                                    HttpServletRequest request, HttpServletResponse response){
//        ReturnResult returnResult = new ReturnResult();
//        log.info("修改订单状态------------->>/mallShop/updateOrderItemsStatus");
//        getParameterMap(request, response);
//        String orderId = request.getParameter("orderId");
//        String status = request.getParameter("status");
//        if (StringUtils.isEmpty(orderId)){
//            returnResult.setMessage("订单id为空！");
//            return returnResult;
//        }if (StringUtils.isEmpty(status)){
//            returnResult.setMessage("状态为空！");
//            return returnResult;
//        }else {
//            if ("10A".equals(status) ||"10B".equals(status) ||"10C".equals(status) ||
//                    "10D".equals(status) ||"10E".equals(status) ){
//                Order order = payService.getOrderId(orderId);
//                if (StringUtils.isNull(order)){
//                    returnResult.setMessage("未查询该订单！");
//                    return returnResult;
//                }else {
//                    mallShopService.updateOrderItemsStatus(orderId,status);
//                    returnResult.setMessage("修改成功！");
//                    returnResult.setStatus(Boolean.TRUE);
//                    return returnResult;
//                }
//            }else {
//                returnResult.setMessage("订单状态错误！");
//                return returnResult;
//            }
//
//        }
//    }
    /**
     * 临时订单（最新）
     * @param cartStr
     * @param addressId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "temporaryOrder")
    @ResponseBody
    public ReturnResult temporaryOrder(String cartStr,String  addressId,
                                       HttpServletRequest request, HttpServletResponse response) {

        ReturnResult returnResult = new ReturnResult();
        log.info("临时订单（最新）------------->>/mallShop/temporaryOrder");
        getParameterMap(request, response);
        String token = request.getHeader("token");
        String userId="";
        if(StringUtils.isNotEmpty(token)) {
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
        }
        ReturnOrder returnOrder = new ReturnOrder();
        List<ResultCart> resultCarts = new ArrayList<>();
        if (StringUtils.isEmpty(cartStr)){
            returnResult.setMessage("cartStr参数不能为空！");
            return returnResult;
        }
        MallAddress mallAddress = null;
        if (StringUtils.isEmpty(addressId)){
            mallAddress = mallShopService.getDefaultAddress(userId);
        }
        if (StringUtils.isNotEmpty(addressId)){
            mallAddress = mallShopService.getMallAddress(addressId);
            if (StringUtils.isNull(mallAddress)){
                returnResult.setMessage("未查询该地址！");
                return returnResult;
            }
        }
        returnOrder.setMallAddress(mallAddress);
        Map<String,String> mapStr = null;
        String newCartStr  = "" ;
        try {
            mapStr = MallUtils.getShopIds(cartStr);
            if (StringUtils.isEmpty(mapStr)){
                returnResult.setMessage("cartStr不可为空！");
                return  returnResult;
            }else {
                //int i = 0;
                for (String shopId:mapStr.keySet()
                ) {
                    //i++;
                    MallShop myMallShop = mallShopService.getMyMallShop(shopId);

                    //System.out.println(shopId+ ":"+myMallShop.getStatus());
                    if (StringUtils.isNotNull(myMallShop) && "10C".equals(myMallShop.getStatus())){
                        newCartStr += shopId +"["+mapStr.get(shopId)+"]"+"-";
                    }else {
                        continue;
                    }
                    //System.out.println(i+":"+newCartStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(newCartStr);
        if (StringUtils.isEmpty(newCartStr)){
            returnResult.setMessage("没有符合的商品！");
            return  returnResult;
        }
        if (newCartStr.contains("-")) {
            String[] cartStrings = newCartStr.split("-");
            for (String cartString:cartStrings
            ) {
                ResultCart resultCart = getResultCart(cartString, mallAddress);
                if (StringUtils.isNull(resultCart)){
                    returnResult.setMessage("数据格式错误！");
                    return returnResult;
                }
                resultCarts.add(resultCart);
            }

        }else {
            ResultCart resultCart = getResultCart(newCartStr,mallAddress);
            if (StringUtils.isNull(resultCart)){
                returnResult.setMessage("数据格式错误！");
                return returnResult;
            }
            resultCarts.add(resultCart);
        }
        returnOrder.setResultCarts(resultCarts);
        //获取交易总额
        //BigDecimal payTotal = new BigDecimal(0);
        String payTotal = "0";
        Map<String,BigDecimal> map = new HashMap<>();
        for (ResultCart resultCart: resultCarts) {
            System.out.println("单个商铺"+resultCart.getPayAmount());
            map.put(resultCart.getShopId(),resultCart.getPayAmount());
            payTotal = new BigDecimal(payTotal).add(resultCart.getPayAmount()).toString();
        }
        System.out.println("总金额"+new BigDecimal(payTotal));
        returnOrder.setOrderTotal(new BigDecimal(payTotal));
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);

        returnResult.setResult(returnOrder);
        return returnResult;
    }


    /**
     * 生成订单项(最新)
     * @param
     * @return
     */
    @RequestMapping(value = "createOrder")
    @ResponseBody
    @LoginRequired
    public ReturnResult createOrder(@CurrentUser AppUser appUser,String cartStr,String addressId,
                                       String payType,String sourcePay,
                                       HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("生成订单项------------->>/mallShop/createOrder");
        getParameterMap(request, response);
        OrderItem orderItem =null;

        //     地址id
        if (StringUtils.isEmpty(addressId)){
            returnResult.setMessage("收货地址不可为空");
            return  returnResult;
        }
        MallAddress mallAddress = mallShopService.getMallAddress(addressId);
        if (StringUtils.isNull(mallAddress)){
            returnResult.setMessage("未查询到该地址！");
            return  returnResult;
        }
        if ("SCZFB".equals(payType) || "SCWX".equals(payType)){

        }else {
            returnResult.setMessage("支付方式错误！");
            return  returnResult;
        }

        Map<String,String> map = null;
        String newCartStr  = "" ;
        try {
            map = MallUtils.getShopIds(cartStr);
            if (StringUtils.isEmpty(map)){
                returnResult.setMessage("cartStr不可为空！");
                return  returnResult;
            }else {
                for (String shopId:map.keySet()
                ) {
                    MallShop myMallShop = mallShopService.getMyMallShop(shopId);
                    if (StringUtils.isNotNull(myMallShop) && "10C".equals(myMallShop.getStatus())){
                        newCartStr += shopId +"["+map.get(shopId)+"]"+"-";
                    }else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(newCartStr);
        if (StringUtils.isEmpty(newCartStr)){
            returnResult.setMessage("没有符合的商品！");
            return  returnResult;
        }
        //判断库存问题
        Map<String, String> stringStringMap = null;
        try {
             stringStringMap = MallUtils.splitCartString(newCartStr);
            if(StringUtils.isEmpty(stringStringMap)){
                returnResult.setMessage("cartStr不可为空！");
                return  returnResult;
            }
            //解决库存问题  key 为规格id specificationById
            for (String key:stringStringMap.keySet()
            ) {
                Specification specificationById = mallShopService.getSpecificationById(key);

                try {
                    if(specificationById.getCommodityReserve() >= Integer.parseInt(stringStringMap.get(key))
                            &&  "10B".equals(specificationById.getStatus())){
                        continue;
                    }else {
                        returnResult.setMessage(specificationById.getCommodityDetail()+"库存不足或是未上架状态！");
                        return  returnResult;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    returnResult.setMessage("cartStr格式中商品数量格式错误！");
                    return  returnResult;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setMessage("cartStr格式错误！");
            return  returnResult;
        }
        //调用临时订单，获取交易总价，商铺收益
        ReturnOrder returnOrderSuccess  = (ReturnOrder) temporaryOrder(newCartStr,addressId,request,response).getResult();
        if (StringUtils.isNull(returnOrderSuccess)){
            returnResult.setMessage("生成订单异常");
            return  returnResult;
        }

        //BigDecimal payTotal = new BigDecimal(0);
        //该订单项总金额
        //BigDecimal payAmount = commodityPrice.multiply(new BigDecimal(commodityNum)).add(fare);
        //System.out.println("该订单项总金额:"+payAmount);
        //orderItem.setShopIncome(payAmount);
        List<ResultCart> resultCarts= returnOrderSuccess.getResultCarts();
        //获取每个商铺id 及收益
        Map<String,BigDecimal> addMoneyToMerchantMap = new HashMap<>();
        if (StringUtils.isNotEmpty(resultCarts)){
            for (ResultCart resultCart:resultCarts
            ) {
                addMoneyToMerchantMap.put(resultCart.getShopId(),resultCart.getPayAmount());
                System.out.println(addMoneyToMerchantMap);
            }
        }
        Order order =new Order();
        //获取交易总额
        order.setMoney(returnOrderSuccess.getOrderTotal());
        order.setTradeType(payType);
        JSONObject jsonObject = null;
        try {
            if (StringUtils.isNotEmpty(sourcePay) && "YYSM".equals(sourcePay)){
                log.info("扫码支付");
                jsonObject = payController.payNative(appUser,order, request, response);
            }else {
                log.info("手机支付");
                jsonObject = payController.payYuYue(order, appUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String orderId = null ;



        if ("true".equals(jsonObject.getString("status"))){
            if (StringUtils.isNotEmpty(sourcePay) && "YYSM".equals(sourcePay)){
                orderId = jsonObject.getString("message");
                returnResult.setMessage(orderId);
            }else if(StringUtils.isEmpty(sourcePay)){
                orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                returnResult.setMessage("订单生成成功！");
            }
            if (StringUtils.isEmpty(orderId)){
                returnResult.setMessage("订单生成失败！！");
                return returnResult;
            }
            returnResult.setResult(jsonObject.get("result"));

        }
        //设置订单项状态
        //Order getOrder = payService.getOrderId(orderId);
        //String getOrderStatus = getOrder.getStatus();
        /*634543A9414EFDBEB63B6BDDB8535D11[488DA0232479449D9FE0571FA4FFB984:2]-
        A0E34543A9414EFDBEB63B6BDDB8156[5FF99665F69C4CE7B33669876395BB7C:1;
        F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]*/
        //生成订单项
        for (String specificationId:stringStringMap.keySet()
        ) {

            orderItem = addOrderItem(specificationId, Integer.parseInt(stringStringMap.get(specificationId)),mallAddress);
            //设置地址id
            orderItem.setAddressId(addressId);
            //消费者id
            orderItem.setConsumerId(appUser.getId());
            //订单id
            orderItem.setOrderId(orderId);
            //支付状态
            orderItem.setStatus("10A");
            //商铺收益
            BigDecimal shopIncome = addMoneyToMerchantMap.get(orderItem.getShopId());
            orderItem.setShopIncome(shopIncome);
            //
            orderItem.setSpecificAddr(mallAddress.getSpecificAddr());
            orderItem.setPhone(mallAddress.getPhone());
            orderItem.setReceiver(mallAddress.getReceiver());
            mallShopService.editMallOrderItem(orderItem);
            mallShopService.deletePayCart(appUser.getId(),specificationId);
        }
        //订单支付给商家加钱
        // Map<String,BigDecimal> map = new HashMap<>();
//        if ("10B".equals(getOrderStatus)){
//            for (String shopId:map.keySet()
//                 ) {
//                MallShop myMallShop = mallShopService.getMyMallShop(shopId);
//                //获取商家id
//                String merchantId = myMallShop.getMerchantId();
//                BigDecimal money = map.get(shopId);
//                AppUser appUserMsg = loginService.getAppUserMsg("", "", merchantId);
//                BigDecimal mIncome = ResultJSONUtils.updateMIncome(appUserMsg, money, "+");
//                payService.updateMIncome(merchantId,mIncome);
//            }
//        }


        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }
    /**
     * 生成订单项
     * @param commodityId
     */
    public OrderItem addOrderItem(String commodityId,int commodityNum,MallAddress mallAddress){
        OrderItem orderItem = new OrderItem();
        //获取规格
        Specification specification = mallShopService.getSpecificationById(commodityId);

        try {
            if (StringUtils.isNotNull(specification)){
                orderItem.setSpecificationJson(JSON.toJSONString(specification));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("转换json串失败！");
            return null;
        }
        //获取规格价格
        BigDecimal commodityPrice = specification.getCommodityPrice();

        //获取商铺id
        String shopId = specification.getShopId();
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        try {
            if (StringUtils.isNotNull(specification)){
                orderItem.setShopJson(JSON.toJSONString(myMallShop));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("转换json串失败！");
            return null;
        }
        //获取运费
        //BigDecimal fare = myMallShop.getFare();

        //生成订单项id
        orderItem.setOrderItemId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        orderItem.setMerchantId(myMallShop.getMerchantId());
        orderItem.setShopId(shopId);
        //规格id
        orderItem.setCommodityId(commodityId);
        //      运费
        BigDecimal fare = getFare(myMallShop.getFeeArea(),mallAddress);
        if (fare.compareTo(new BigDecimal(0)) == 0 ){
            orderItem.setFare(myMallShop.getFare());
        }else {
            orderItem.setFare(fare);
        }

        //      规格价格
        orderItem.setCommodityPrice(commodityPrice);
        //添加购买商品数量
        orderItem.setCommodityNum(commodityNum);

        return orderItem;


    }



    /**
     *顾客订单(顾客订单)
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMerchantOrder")
    @ResponseBody
    @LoginRequired
    public ReturnResult getMerchantOrder(@CurrentUser  AppUser appUser,
                                  HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("顾客订单------------->>/mallShop/getMerchantOrder");
        getParameterMap(request, response);
        //String orderId = request.getParameter("orderId");
        //订单状态（做筛选用）
        String status = request.getParameter("status");
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";
        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        //获取商户的订单项
        List<OrderItemVo> merchantOrder = mallShopService.getMerchantOrder(appUser.getId());
        if (StringUtils.isNotEmpty(merchantOrder)){
            for (OrderItemVo orderItemVo: merchantOrder
                 ) {
                System.out.println(orderItemVo);
                Order order = payService.getOrderId(orderItemVo.getOrderId());
                AppUser appUserMsg = loginService.getAppUserMsg("", "", orderItemVo.getConsumerId());
                //MallAddress mallAddress = mallShopService.getMallAddress(orderItemVo.getAddressId());
                orderItemVo.setConsumerName(appUserMsg.getNickName());
                orderItemVo.setConsumerPhone(appUserMsg.getPhone());
                orderItemVo.setOrderNo(order.getOrderNo());
                orderItemVo.setTradeType(order.getTradeType());
                String sJson = orderItemVo.getSpecificationJson();

                try {
                    if(StringUtils.isNotEmpty(sJson)){
                        Specification specification = JSONObject.parseObject(sJson, Specification.class);
                        orderItemVo.setSpecification(specification);
                    }else {
                        orderItemVo.setSpecification(null);
                    }
                } catch (Exception e) {
                    log.info("json格式转换错误！");
                    e.printStackTrace();
                }
                MallAddress mallAddress =  new MallAddress();
                mallAddress.setSpecificAddr(orderItemVo.getSpecificAddr());
                mallAddress.setPhone(orderItemVo.getPhone());
                mallAddress.setReceiver(orderItemVo.getReceiver());
                orderItemVo.setMallAddress(mallAddress);
            }
        }

        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(merchantOrder);
        return returnResult;

    }


    /**
     *顾客订单(第一版弃用)
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOrderByMerchantId")
    @ResponseBody
    @LoginRequired
    public ReturnResult getOrderByMerchantId(@CurrentUser  AppUser appUser,
                                         HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("顾客订单(第一版弃用)------------->>/mallShop/getOrderByMerchantId");
        getParameterMap(request, response);
        return returnResult;
        //String orderId = request.getParameter("orderId");
        //订单状态
//        String status = request.getParameter("status");
//        String orderNo = request.getParameter("orderId");
//        //通过shopId获取该商铺的全部订单
//        List<String> orderByShopId = null;
//        //将结果打包
//        List<ReturnOrder> returnOrders = new ArrayList<>();
//        //获取我的商铺id
//        List<MallShop> mallShops = mallShopService.myMallShopInfo(appUser.getId());
//        if (StringUtils.isEmpty(mallShops)){
//            returnResult.setMessage("暂无商铺！");
//            returnResult.setStatus(Boolean.TRUE);
//            return returnResult;
//        }
//        for (MallShop mallShop:mallShops
//        ) {
//            String shopId = mallShop.getShopId();
//            //获取商铺订单列表
//            if (StringUtils.isEmpty(orderNo)){
//                orderByShopId = mallShopService.getOrderToItem(shopId,"",status);
//            }else {
//                orderByShopId.add(orderNo);
//            }if (StringUtils.isEmpty(orderByShopId)){
//                continue;
//            }else {
//                for (String orderId:orderByShopId
//                ) {
//                    Order order = payService.getOrderId(orderId);
//                    if ("10A".equals(order.getStatus()) ||"10C".equals(order.getStatus()) || "10D".equals(order.getStatus())){
//                        continue;
//                    }
//                    ReturnOrder returnOrder =new ReturnOrder();
//                    //根据订单id获取订单项
//                    List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId,shopId,"");
//                    List<Specification> commodities = new ArrayList<>();
//                    String  payAmount ="0";
//                    for (OrderItem orderItem:mallOrderItems
//                    ) {
//                        Specification specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
//                        //设置规格价格
//                        specificationById.setCommodityPrice(orderItem.getCommodityPrice());
//                        //设置规格购买数量
//                        specificationById.setCommodityNum(orderItem.getCommodityNum());
//                        //设置支付状态
//                        specificationById.setStatus(order.getStatus());
//                        payAmount = orderItem.getCommodityPrice().multiply(BigDecimal.valueOf(orderItem.getCommodityNum())).toString();
//                        commodities.add(specificationById);
//                    }
//                    //去订单号
//
//                    AppUser appUserMsg = loginService.getAppUserMsg("", "",  mallOrderItems.get(0).getConsumerId());
//                    //消费者名
//                    returnOrder.setConsumerName(appUserMsg.getNickName());
//                    returnOrder.setConsumerPhone(appUserMsg.getPhone());
//                    returnOrder.setOrderNo(order.getOrderNo());
//                    returnOrder.setOrderId(order.getId());
//                    returnOrder.setCommodities(commodities);
//                    returnOrder.setPayAmount(new BigDecimal(payAmount).add(mallOrderItems.get(0).getFare()));
//                    returnOrder.setFare(mallOrderItems.get(0).getFare());
//                    //下单时间
//                    returnOrder.setCreateTime(mallOrderItems.get(0).getCreateTime());
//                    //支付类型
//                    returnOrder.setTradeType(order.getTradeType());
//                    returnOrder.setStatus(mallOrderItems.get(0).getStatus());
//                    returnOrders.add(returnOrder);
//                }
//            }
//        }
//        returnResult.setMessage("查询成功！");
//        returnResult.setStatus(Boolean.TRUE);
//        returnResult.setResult(returnOrders);
//        return returnResult;





    }
    /**
     * 红点提示获取未发货的订单
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getNoPayOrderItem")
    @ResponseBody
    @LoginRequired
    public ReturnResult  getNoPayOrderItem(@CurrentUser  AppUser appUser,
                                           HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("商户获取所有订单------------->>/mallShop/getNoPayOrderItem");
        getParameterMap(request, response);



        String noPayOrderItem = mallShopService.getNoPayOrderItem(appUser.getId());
        returnResult.setResult(noPayOrderItem);
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;

    }

    /**
     *第二版商户 获取 订单详情
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOrderDetailByOrderId")
    @ResponseBody
    @LoginRequired
    public ReturnResult getOrderDetailByOrderId(@CurrentUser  AppUser appUser,
                                                HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("订单详情(第二版 商户获取)------------->>/mallShop/getOrderDetailByOrderId");
        getParameterMap(request, response);
        String orderItemId = request.getParameter("orderItemId");
        OrderItemVo orderItemVo = mallShopService.getMallOrderItemById(orderItemId);
        if (StringUtils.isNull(orderItemVo)){
            returnResult.setMessage("查无该订单！");
            return returnResult;
        }

        Order order = payService.getOrderId(orderItemVo.getOrderId());
        AppUser appUserMsg = loginService.getAppUserMsg("", "", orderItemVo.getConsumerId());
//        MallAddress mallAddress = mallShopService.getMallAddress(orderItemVo.getAddressId());

        String sJson = orderItemVo.getSpecificationJson();
        MallAddress mallAddress = new MallAddress();;
        try {
            if (StringUtils.isNotEmpty(sJson)){
                Specification specification = JSONObject.parseObject(sJson, Specification.class);
                orderItemVo.setSpecification(specification);
            }else {
                orderItemVo.setSpecification(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("json格式转换错误！");
        }
        mallAddress.setReceiver(orderItemVo.getReceiver());
        mallAddress.setPhone(orderItemVo.getPhone());
        mallAddress.setSpecificAddr(orderItemVo.getSpecificAddr());
        orderItemVo.setConsumerName(appUserMsg.getNickName());
        orderItemVo.setConsumerPhone(appUserMsg.getPhone());
        orderItemVo.setOrderNo(order.getOrderNo());
        orderItemVo.setTradeType(order.getTradeType());
        orderItemVo.setMallAddress(mallAddress);

        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(orderItemVo);
        return returnResult;

    }

    /**
     *商户 获取 订单详情
     * @param appUser
     * @param request
     * @param response
     * @return
     */
//   /* @RequestMapping(value = "getOrderDetailByOrderId")
//    @ResponseBody
//    @LoginRequired
//    public ReturnResult getOrderDetailByOrderId(@CurrentUser  AppUser appUser,
//                                         HttpServletRequest request, HttpServletResponse response){
//
//        ReturnResult returnResult = new ReturnResult();
//        log.info("商户 获取 订单详情------------->>/mallShop/getOrderDetailByOrderId");
//        getParameterMap(request, response);
//        String orderId = request.getParameter("orderId");
//        Order order = payService.getOrderId(orderId);
//        if (StringUtils.isNull(order)){
//            returnResult.setMessage("查无该订单！");
//            return returnResult;
//        }
//        //获取商铺订单列表
//        ReturnOrderDetail returnOrderDetail=new  ReturnOrderDetail();
//
//        returnOrderDetail.setOrderId(orderId);
//        returnOrderDetail.setOrderNo(order.getOrderNo());
//        String createTime = order.getCreateTime();
//        System.out.println(createTime);
//        if (StringUtils.isNotNull(createTime)){
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            Date date= null;
//            try {
//                date = formatter.parse(createTime);
//                returnOrderDetail.setCreateTime(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//        }
//        List<Specification> commodities = new ArrayList<>();
//        List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId, "","");
//        String addressId = mallOrderItems.get(0).getAddressId();
//        MallAddress mallAddress = mallShopService.getMallAddress(addressId);
//        returnOrderDetail.setMallAddress(mallAddress);
//        for (OrderItem orderItem:mallOrderItems
//                ) {
//                    Specification specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
//                    //设置规格价格
//                    specificationById.setCommodityPrice(orderItem.getCommodityPrice());
//                    //设置规格购买数量
//                    specificationById.setCommodityNum(orderItem.getCommodityNum());
//                    commodities.add(specificationById);
//                }
//        returnOrderDetail.setStatus(mallOrderItems.get(0).getStatus());
//        returnOrderDetail.setPayAmount(mallOrderItems.get(0).getShopIncome());
//        returnOrderDetail.setCommodities(commodities);
//        returnResult.setMessage("查询成功！");
//        returnResult.setStatus(Boolean.TRUE);
//        returnResult.setResult(returnOrderDetail);
//        return returnResult;
//
//    }*/

    /**
     *我的消费
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOrderByConsumerId")
    @ResponseBody
    @LoginRequired
    public ReturnResult getOrderByConsumerId(@CurrentUser  AppUser appUser,
                                                HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("我的消费------------->>/mallShop/getOrderByConsumerId");
        getParameterMap(request, response);

        String status = request.getParameter("status");
        String consumerId = appUser.getId();
        //获取商城中我的订单列表
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";
        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        List<Order> scOrder = payService.getSCOrder(consumerId, status);
        List<ReturnOrderDetail> returnOrderDetailList = new ArrayList<>();
        if (StringUtils.isNull(scOrder)){
            returnResult.setMessage("暂无订单！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(scOrder);
            return returnResult;
        }
        //订单
        for (Order order:scOrder
             ) {
            //订单状态为10A的不展示了（老板不要）
            if ("10A".equals(order.getStatus()) || "10C".equals(order.getStatus()) ||"10D".equals(order.getStatus())){
                continue;
            }
            String orderId = order.getId();
            ReturnOrderDetail returnOrderDetail = new ReturnOrderDetail();
            String createTime = order.getCreateTime();
            if (StringUtils.isNotNull(createTime)){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date= null;
                try {
                    date = formatter.parse(createTime);
                    returnOrderDetail.setCreateTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            List<Specification> commodities = new ArrayList<>();
            //获取订单中每个订单项
            List<OrderItem> orderItems= mallShopService.getMallOrderItem(orderId,"","");
            if (StringUtils.isEmpty(orderItems)){
               continue;
            }
            Specification specificationById = null;
            String shopJson = "";
            for (OrderItem orderItem:orderItems
                 ) {
                //specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                String specificationJson = orderItem.getSpecificationJson();
                shopJson = orderItem.getShopJson();
                try {
                    if (StringUtils.isNotEmpty(specificationJson)){
                        specificationById = JSONObject.parseObject(specificationJson,Specification.class);
                    }else {
                        specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("我的消费json转换错误！");
                }

                if ("10B".equals(order.getStatus()) && "10A".equals(orderItem.getStatus())){
                    orderItem.setStatus("10B");
                    mallShopService.editMallOrderItem(orderItem);
                }
                //设置规格购买数量
                specificationById.setCommodityNum(orderItem.getCommodityNum());
                //设置规格价格
                specificationById.setCommodityPrice(orderItem.getCommodityPrice());
                //设置订单的状态
                specificationById.setStatus(order.getStatus());
                commodities.add(specificationById);
                MallComment isComment = mallShopService.getMallComment(specificationById.getShopId(),orderId, appUser.getId());
                if (StringUtils.isNotNull(isComment)){
                    specificationById.setIsComment(Boolean.TRUE);
                }else {
                    specificationById.setIsComment(Boolean.FALSE);
                }
            }

            returnOrderDetail.setOrderId(order.getId());
            returnOrderDetail.setOrderNo(order.getOrderNo());
            returnOrderDetail.setPayAmount(order.getMoney());
            returnOrderDetail.setCommodities(commodities);
            returnOrderDetail.setFare(orderItems.get(0).getFare());
            returnOrderDetail.setStatus(orderItems.get(0).getStatus());
            returnOrderDetail.setTradeType(order.getTradeType());
            MallShop myMallShop = new MallShop();
            try {
                if (StringUtils.isNotEmpty(shopJson)){
                    myMallShop = JSONObject.parseObject(shopJson,MallShop.class);
                }else {
                    myMallShop = mallShopService.getMyMallShop(orderItems.get(0).getShopId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("我的消费json转换错误！");
            }

            returnOrderDetail.setMerchantAddr(myMallShop.getMerchantAddr());
            returnOrderDetail.setMerchantPhone(myMallShop.getMerchantPhone());
            returnOrderDetailList.add(returnOrderDetail);
        }

        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(returnOrderDetailList);
        return returnResult;

    }
    /**
     *消费者 再次支付
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "againPay")
    @ResponseBody
    @LoginRequired
    public ReturnResult againPay(@CurrentUser  AppUser appUser,
                                             HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("消费者 再次支付------------->>/mallShop/againPay");
        getParameterMap(request, response);

        String orderId = request.getParameter("orderId");
//        String tradeType = request.getParameter("tradeType");
//        if ("SCZFB".equals(tradeType) || "SCWX".equals(tradeType)){
//
//        }else {
//            returnResult.setMessage("支付类型错误！");
//            return  returnResult;
//        }
        Order order = payService.getOrderId(orderId);
        if (StringUtils.isNull(order)){
            returnResult.setMessage("为查询该订单！");
            return returnResult;
        }
        JSONObject jsonObject = null;
        if ("10A".equals(order.getStatus())){
            if ("SCWX".equals(order.getTradeType())) {
                try {
                    jsonObject = payController.payWX(order);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("SCZFB".equals(order.getTradeType())) {
                try {
                    jsonObject = payController.payZFB(order);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if ("true".equals(jsonObject.getString("status"))){
                orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                returnResult.setResult(jsonObject.get("result"));
                returnResult.setStatus(Boolean.TRUE);
                returnResult.setMessage("返回成功！");
            }
        }else if ("10B".equals(order.getStatus())){
            returnResult.setMessage("该订单已支付！");
            returnResult.setStatus(Boolean.TRUE);
        }else {
            returnResult.setMessage("该订单已失效！");
            returnResult.setStatus(Boolean.TRUE);
        }
        return returnResult;
    }




    /**
     * 临时订单（最新）获取返回ResultCart结果
     *
     * @param cartStr  传入数据格式如下
     * A0E34543A9414EFDBEB63B6BDDB8156
     *[5FF99665F69C4CE7B33669876395BB7C:1;F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]
     * @param
     *
     * @return
     */
    public ResultCart getResultCart(String cartStr,MallAddress  mallAddress){
        ResultCart resultCart = new ResultCart();

        //获取shopId
        String shopId = cartStr.substring(0, cartStr.indexOf("["));
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        if (StringUtils.isNull(myMallShop)){
            return null;
        }
        //获取收费区域
        String feeArea = myMallShop.getFeeArea();
        //获取运费
        BigDecimal getFare =  getFare(feeArea,mallAddress);
        System.out.println("匹配后的价格："+getFare);
        //设置运费
        if (getFare.compareTo(new BigDecimal(-1)) == 0){
            return null;
        }
        if (getFare.compareTo(BigDecimal.ZERO) ==0) {
            resultCart.setFare(myMallShop.getFare());
            System.out.println("匹配全国包邮后的价格："+myMallShop.getFare());
        }else {
            resultCart.setFare(getFare);
            System.out.println("匹配后部分收费区域的价格："+getFare);
        }
        //设置shopId
        resultCart.setShopId(shopId);
        //设置商铺名
        resultCart.setCommodityName(myMallShop.getCommodityName());
        try {
            String commodityIds = cartStr.substring( cartStr.indexOf("[")+1,cartStr.lastIndexOf("]"));
            //设置规格列表
            List<Specification> commodities = new ArrayList<>();
            if (cartStr.contains(";")){
                //有多个商品
                String[] commodityInfos = commodityIds.split(";");
                String payTotal = "0";
                for (String commodityInfo:commodityInfos
                ) {
                    String commodityId = commodityInfo.split(":")[0];
                    String commodityNum = commodityInfo.split(":")[1];
                    Specification specificationById = mallShopService.getSpecificationById(commodityId);
                    specificationById.setCommodityNum(Integer.parseInt(commodityNum));
                    commodities.add(specificationById);
                    payTotal = new BigDecimal(payTotal).add(specificationById.getCommodityPrice().multiply(new BigDecimal(commodityNum))).toString();
                }
                resultCart.setCommodities(commodities);
                //商品总价
                resultCart.setCommodityAmount(new BigDecimal(payTotal));
                //商品总价加运费
                resultCart.setPayAmount(new BigDecimal(payTotal).add(resultCart.getFare()));
            }else {
                //仅有一个商品
                String commodityId = commodityIds.split(":")[0];
                String commodityNum = commodityIds.split(":")[1];
                Specification specificationById = mallShopService.getSpecificationById(commodityId);
                specificationById.setCommodityNum(Integer.parseInt(commodityNum));
                commodities.add(specificationById);
                BigDecimal payTotal = specificationById.getCommodityPrice().multiply(new BigDecimal(commodityNum));
                //商品总价
                resultCart.setCommodityAmount(payTotal);
                //商品总价加运费
                resultCart.setPayAmount(payTotal.add(resultCart.getFare()));
                resultCart.setCommodities(commodities);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("参数格式错误！");
            return null;
        }

        return resultCart;
    }
    //获取运费
    public BigDecimal getFare(String feeArea,MallAddress mallAddress){

        //如果收费区域为空 或是地址为空 ，使用商家设置的运费
        if (StringUtils.isEmpty(feeArea) || StringUtils.isNull(mallAddress)){
            return new BigDecimal(0);
        }
        String specificAddr = mallAddress.getSpecificAddr();
        try{
            String substring = specificAddr.substring(0, specificAddr.indexOf("-"));
            if (feeArea.contains(substring)){
                if (feeArea.contains(";")){
                    String[] split = feeArea.split(";");
                    for (String s:split
                    ) {
                        if (s.contains(substring)){
                            //运费价格
                            String fare = s.split(":")[1];
                            System.out.println("匹配成功！-"+fare);
                            return new BigDecimal(fare);
                        }
                    }
                    //未匹配到收费区域，返回0
                    return new BigDecimal(0);
                }else {
                    if (feeArea.contains(substring)){
                        String fare = feeArea.split(":")[1];
                        System.out.println("匹配成功！");
                        return new BigDecimal(fare);
                    }else {
                        System.out.println("匹配失败！");
                        return new BigDecimal(0);
                    }
                }
            }else {
                return new BigDecimal(0);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("数据格式错误！");
            return new BigDecimal(-1);
        }

    }


    /**
     *获取我的收货地址
     * @param appUser
     * @param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMyAddress")
    @ResponseBody
    @LoginRequired
    public ReturnResult getMyAddress(@CurrentUser  AppUser appUser,
                                    HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();

        log.info("获取我的收货地址------------->>/mallShop/getMyAddress");
        String addressId = request.getParameter("addressId");
        getParameterMap(request, response);
        if (StringUtils.isNotEmpty(addressId)){
            MallAddress mallAddress = mallShopService.getMallAddressByStatus(addressId);
            returnResult.setResult(mallAddress);
        }else {
            List<MallAddress> mallAddrByUserId = mallShopService.getMallAddrByStatus(appUser.getId());
            returnResult.setResult(mallAddrByUserId);
        }
        returnResult.setMessage("返回成功");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


    /**
     *编辑我的地址（添加修改）
     * @param appUser
     * @param mallAddress
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "editMyAddress")
    @ResponseBody
    @LoginRequired
    public ReturnResult editMyAddress(@CurrentUser  AppUser appUser,MallAddress mallAddress,
                                     HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("编辑我的地址（添加修改）------------->>/mallShop/editMyAddress");
        getParameterMap(request, response);
        //添加
        if (StringUtils.isEmpty(mallAddress.getAddressId())){
            List<MallAddress> mallAddrByUserId = mallShopService.getMallAddrByUserId(appUser.getId());
            if (StringUtils.isEmpty(mallAddrByUserId)){
                mallAddress.setDefaultAddr("1");
            }else {
                mallAddress.setDefaultAddr("0");
            }
            if (StringUtils.isEmpty(mallAddress.getSpecificAddr())){
                returnResult.setMessage("收货地址不可为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(mallAddress.getPhone())){
                returnResult.setMessage("手机号不可为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(mallAddress.getReceiver())){
                returnResult.setMessage("收货人不可为空！");
                return returnResult;
            }
            mallAddress.setAddressId(UUID.randomUUID().toString().replace("-","").toUpperCase());
            returnResult.setMessage("添加成功");
            mallAddress.setUserId(appUser.getId());
            mallAddress.setStatus("Y");
            mallShopService.editMallAddr(mallAddress);
            returnResult.setStatus(Boolean.TRUE);
            return returnResult;

        }else {
            //修改地址
            MallAddress myMallAddress = mallShopService.getMallAddress(mallAddress.getAddressId());
            if (StringUtils.isNull(myMallAddress)){
                returnResult.setMessage("未查询到该地址！");
                return returnResult;
            } if (StringUtils.isNotEmpty(mallAddress.getSpecificAddr())){
                myMallAddress.setSpecificAddr(mallAddress.getSpecificAddr());
            } if (StringUtils.isNotEmpty(mallAddress.getPhone())){
                myMallAddress.setPhone(mallAddress.getPhone());
            } if (StringUtils.isNotEmpty(mallAddress.getReceiver())){
                myMallAddress.setReceiver(mallAddress.getReceiver());
            } if (StringUtils.isNotEmpty(mallAddress.getZipCode())){
                myMallAddress.setZipCode(mallAddress.getZipCode());
            } if (StringUtils.isNotEmpty(mallAddress.getDefaultAddr())){
                if ("1".equals(mallAddress.getDefaultAddr())){
                    mallShopService.changeDefaultAddr(appUser.getId());
                }
                myMallAddress.setDefaultAddr(mallAddress.getDefaultAddr());
            }
            myMallAddress.setStatus("Y");
            returnResult.setMessage("修改成功！");
            mallShopService.editMallAddr(myMallAddress);
            returnResult.setStatus(Boolean.TRUE);
            return returnResult;
        }


    }

    /**
     * 通过地址id删除地址
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteMyAddressById")
    @ResponseBody
    @LoginRequired
    public ReturnResult deleteMyAddressById(@CurrentUser  AppUser appUser,
                                      HttpServletRequest request, HttpServletResponse response){
        String addressId = request.getParameter("addressId");

        ReturnResult returnResult = new ReturnResult();
        log.info("通过地址id删除地址------------->>/mallShop/deleteMyAddressById");
        getParameterMap(request, response);
        mallShopService.deleteMallAddr(addressId);
        returnResult.setMessage("删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


}
