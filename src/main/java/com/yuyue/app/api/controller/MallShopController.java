package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.MallShopService;
import com.yuyue.app.api.service.MyService;
import com.yuyue.app.api.service.PayService;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.utils.GouldUtils;
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

@Slf4j
@RestController
@RequestMapping(value = "/mallShop" , produces = "application/json; charset=UTF-8")
public class MallShopController extends BaseController{

    private static  final  java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");


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
        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(myMallShop);
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
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";
        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        List<MallShop> allMallShop = mallShopService.getAllMallShop(myArea,content);

        List<MallShopVo> list = GouldUtils.getNearbyStoreByDistinceAsc(sortType, new BigDecimal(gdLon), new BigDecimal(gdLat), allMallShop);
        returnResult.setResult(list);
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
        if (StringUtils.isEmpty(shopId)){
            returnResult.setMessage("商铺id为空");
            return returnResult;
        }
        /*---------------------------------生成订单-------------------------------*/
        String priceId = request.getParameter("priceId");
        String tradeType = request.getParameter("tradeType");
        List<AdPrice> advertisementFeeInfo = myService.getAdvertisementFeeInfo(priceId);
        if (StringUtils.isEmpty(advertisementFeeInfo)){
            returnResult.setMessage("价格id传入错误！！");
            return returnResult;
        }

        AdPrice adPrice = advertisementFeeInfo.get(0);
        BigDecimal bigDecimal = new BigDecimal(adPrice.getAdTotalPrice()).multiply(new BigDecimal(adPrice.getAdDiscount()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        Order order = new Order();
        order.setTradeType(tradeType);
        order.setMoney(bigDecimal);
        //传入商品id重新支付
        JSONObject jsonObject = null;
        /*---------------------------------生成订单结束-------------------------------*/
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        System.out.println(StringUtils.isNull(myMallShop));
        if (StringUtils.isNotNull(myMallShop) && StringUtils.isNotEmpty(myMallShop.getStatus())){
            //商铺未支付状态或是 支付超时状态
            if ("10A".equals(myMallShop.getStatus())  || "10D".equals(myMallShop.getStatus())){
                if(StringUtils.isNotEmpty(myMallShop.getOrderId())){
                    Order getOrder = payService.getOrderId(myMallShop.getOrderId());
                    if (StringUtils.isNull(getOrder)){
                        returnResult.setMessage("未查询该订单！！");
                        return returnResult;
                    }else if("10B".equals(getOrder.getStatus()) && "10A".equals(myMallShop.getStatus()) ){
                        //修改商铺状态
                        myMallShop.setStatus("10B");
                        mallShopService.insertMyMallShop(myMallShop);
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
                    //支付超时状态    支付失败   商铺到期   -->重新生成新的订单
                    else if ("10D".equals(myMallShop.getStatus())  ||
                                 "10C".equals(getOrder.getStatus()) ||
                                     "10D".equals(getOrder.getStatus())){

                        try {
                            jsonObject = payController.payYuYue(order, user);
                            //生成订单
                            if ("true".equals(jsonObject.getString("status"))){
                                //成功生成新的订单，获取订单ID
                                String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                                if (StringUtils.isEmpty(orderId)){
                                    returnResult.setMessage("订单Id为空！！");
                                    return returnResult;
                                }
                                myMallShop.setOrderId(orderId);
                                Order newOrder = payService.getOrderId(orderId);
                                if ("10B".equals(newOrder.getStatus()))
                                    myMallShop.setStatus("10B");
                                else
                                    myMallShop.setStatus("10A");
                                //returnResult.setResult(jsonObject.get("result"));
                                returnResult.setMessage("订单重新生成，等待审核！！");
                                returnResult.setStatus(Boolean.TRUE);
                                mallShopService.insertMyMallShop(myMallShop);
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
                return returnResult;
            }
            if ("10B".equals(myMallShop.getStatus())){
                returnResult.setMessage("已添加,待审核！");
                returnResult.setStatus(Boolean.TRUE);
            }
            if ("10C".equals(myMallShop.getStatus())){
                returnResult.setMessage("该订单正在发布！");
                returnResult.setStatus(Boolean.TRUE);
            }
            return returnResult;
        }else {
            //新的商铺申请
            if (StringUtils.isEmpty(shopId)){
                returnResult.setMessage("商铺id不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("category"))){
                returnResult.setMessage("商品/服务分类不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("commodityName"))){
                returnResult.setMessage("商品/服务名称不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("detail"))){
                returnResult.setMessage("商品/服务介绍不能为空！");
                return returnResult;
            }else if (StringUtils.isEmpty(request.getParameter("serviceType"))){
                returnResult.setMessage("服务方式不能为空！");
                return returnResult;
            }
            else if (StringUtils.isEmpty(request.getParameter("fare"))){
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
            mallShop.setShopId(shopId);
            mallShop.setMerchantId(user.getId());
            mallShop.setCategory(request.getParameter("category"));
            mallShop.setCommodityName(request.getParameter("commodityName"));
            String commodityImage = request.getParameter("images");
            if (StringUtils.isNotEmpty(commodityImage)){
                String[] images = commodityImage.split(";");

                for ( Byte i = 0 ; i < images.length ; i++) {
                    ShopImage shopImage = new ShopImage();
                    shopImage.setImagePath(images[i]);
                    shopImage.setImageSort(i);
                    shopImage.setShopId(shopId);
                    System.out.println(images[i]);
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

            mallShop.setServiceType(request.getParameter("serviceType"));
            mallShop.setFare(new BigDecimal(request.getParameter("fare")));
            mallShop.setVideoPath(request.getParameter("remark"));
            mallShop.setBusinessTime(request.getParameter("businessTime"));
            mallShop.setBusinessStatus(request.getParameter("businessStatus"));
            mallShop.setMerchantAddr(request.getParameter("merchantAddr"));
            mallShop.setMerchantPhone(request.getParameter("merchantPhone"));
            mallShop.setServiceArea(request.getParameter("serviceArea"));
            mallShop.setFeeArea(request.getParameter("feeArea"));
            mallShop.setVideoPath(request.getParameter("videoPath"));
            mallShop.setRemark(request.getParameter("remark"));
            mallShopService.insertMyMallShop(mallShop);
            /*----------------------------------------接支付------------------------------------------------*/
            //新的商品推广申请
               try {
                  jsonObject = payController.payYuYue(order, user);
                  //生成订单
                   if ("true".equals(jsonObject.getString("status"))){
                       String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                       if (StringUtils.isEmpty(orderId)){
                           returnResult.setMessage("订单Id为空！！");
                           return returnResult;
                       }
                            mallShop.setOrderId(orderId);
                            Order getOrder = payService.getOrderId(orderId);
                            if ("10B".equals(getOrder.getStatus()))
                                mallShop.setStatus("10B");
                            else
                                mallShop.setStatus("10A");
                            returnResult.setMessage("订单生成，等待审核！！");
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
            return returnResult;
        }

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
    public ReturnResult updateMyMallShopInfo(MallShop mallShop,HttpServletRequest request, HttpServletResponse response){
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
        }else if (StringUtils.isEmpty(mallShop.getServiceType())){
            returnResult.setMessage("服务方式不能为空！");
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
        mallShopService.updateMyMallShopInfo(mallShop);

        returnResult.setMessage("修改成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }

    /**
     * 修改商铺图片
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
        List<Specification> specification = mallShopService.getSpecification(shopId);
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
            System.out.println(myMallShop);
            if (StringUtils.isEmpty(resultCarts)) {

                cart.setSpecification(mallShopService.getSpecificationById(cart.getCommodityId()));
                ResultCart resultCart = new ResultCart();
                resultCart.setShopId(myMallShop.getShopId());
                resultCart.setCommodityName(myMallShop.getCommodityName());
                List<Cart> newCarts = new ArrayList<>();
                newCarts.add(cart);
                resultCart.setCommodityList(newCarts);
                resultCarts.add(resultCart);
                continue;
            }
            Boolean status = false;
            for (ResultCart resultCart : resultCarts
            ) {

                if (resultCart.getShopId().equals(myMallShop.getShopId())) {
                    cart.setSpecification(mallShopService.getSpecificationById(cart.getCommodityId()));
                    List<Cart> addCarts = resultCart.getCommodityList();
                    addCarts.add(cart);
                    resultCart.setCommodityList(addCarts);
                    status =true;
                    break;
                }
            }
            System.out.println(status);
            if (status == false) {
                cart.setSpecification(mallShopService.getSpecificationById(cart.getCommodityId()));
                ResultCart resultCart1 = new ResultCart();
                resultCart1.setShopId(myMallShop.getShopId());
                resultCart1.setCommodityName(myMallShop.getCommodityName());
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
            returnResult.setMessage("商品id为空！");
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


    @RequestMapping(value = "deleteCart")
    @ResponseBody
    @LoginRequired
    public ReturnResult deleteCart(Cart cart,@CurrentUser  AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        log.info("修改规格------------->>/mallShop/deleteCart");
        getParameterMap(request, response);
        getParameterMap(request, response);
        cart.setConsumerId(appUser.getId());

        mallShopService.deleteCart(cart);
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

        MallComment isComment = mallShopService.getMallComment(mallComment.getShopId(), appUser.getId());
        if (StringUtils.isNotNull(isComment)){
            returnResult.setMessage("该商品已评价！");
            return returnResult;
        }
        if (StringUtils.isEmpty(mallComment.getShopId())){
            returnResult.setMessage("shopId（商品）不能为空！");
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

        mallComment.setCommentId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        mallComment.setConsumerId(appUser.getId());
        mallShopService.addMallComment(mallComment);
        double score = mallShopService.getScore(mallComment.getShopId());
        score = (double) Math.round(score * 10) / 10;
        MallShop mallShop = new MallShop();
        mallShop.setScore(score);
        mallShop.setShopId(mallComment.getShopId());
        mallShopService.updateMyMallShopInfo(mallShop);
        returnResult.setMessage("评价成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


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
        OrderItem orderItem = new OrderItem();

        //      地址id
        if (StringUtils.isEmpty(addressId)){
            returnResult.setMessage("收货地址不可为空");
            return  returnResult;
        }if ("SCZFB".equals(payType) || "SCWX".equals(payType)){

        }else {
            returnResult.setMessage("支付方式错误！");
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

        try {
            if (cartStr.contains("-")){
                String[] split = cartStr.split("-");
                for (String s: split
                ) {
                     String[] split1 = s.split(":");
                    orderItem = addOrderItem(split1[0], Integer.parseInt(split1[1]),addressId);
                    orderItem.setAddressId(addressId);
                    //      消费者id
                    orderItem.setConsumerId(appUser.getId());
                    orderItem.setOrderId(orderId);
                    orderItem.setStatus(getPayStatus.getStatus());
                    mallShopService.editMallOrderItem(orderItem);
                }
            }else {
                String[] split = cartStr.split(":");
                 orderItem = addOrderItem(split[0], Integer.parseInt(split[1]),addressId);
                 orderItem.setAddressId(addressId);
                 //      消费者id
                 orderItem.setConsumerId(appUser.getId());
                 orderItem.setStatus(getPayStatus.getStatus());
                 orderItem.setOrderId(orderId);
                 mallShopService.editMallOrderItem(orderItem);

            }
        }catch (Exception e){
            e.printStackTrace();
            returnResult.setMessage("cartStr格式异常");
            return  returnResult;
        }
        returnResult.setMessage("订单生成成功！");
        returnResult.setStatus(Boolean.TRUE);
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
    public ReturnResult createOrder(@CurrentUser  AppUser appUser,String cartStr,String addressId,
                                       String payType,
                                       HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("生成订单项------------->>/mallShop/createOrder");
        getParameterMap(request, response);
        OrderItem orderItem = new OrderItem();
        //      地址id
        if (StringUtils.isEmpty(addressId)){
            returnResult.setMessage("收货地址不可为空");
            return  returnResult;
        }if ("SCZFB".equals(payType) || "SCWX".equals(payType)){

        }else {
            returnResult.setMessage("支付方式错误！");
            return  returnResult;
        }

        List<ResultCart> getOrder = (List<ResultCart>) temporaryOrder(cartStr,addressId,request,response).getResult();
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
        /*634543A9414EFDBEB63B6BDDB8535D11[488DA0232479449D9FE0571FA4FFB984:2]-
        A0E34543A9414EFDBEB63B6BDDB8156[5FF99665F69C4CE7B33669876395BB7C:1;
        F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]*/
        //生成订单项
        try {
            if (cartStr.contains("-")){
                String[] split = cartStr.split("-");
                for (String s: split
                ) {
                    String commodityIds = s.substring( s.indexOf("[")+1,s.lastIndexOf("]"));

                    if(commodityIds.contains(";")){
                        String[] commodityInfos = commodityIds.split(";");
                        for (String commodityInfo:commodityInfos
                             ) {
                            String[] split1 = commodityInfo.split(":");
                            orderItem = addOrderItem(split1[0], Integer.parseInt(split1[1]),addressId);
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
                    }else {
                        String[] split1 = commodityIds.split(":");
                        orderItem = addOrderItem(split1[0], Integer.parseInt(split1[1]),addressId);
                        //设置地址id
                        orderItem.setAddressId(addressId);
                        //消费者id
                        orderItem.setConsumerId(appUser.getId());
                        //订单id
                        orderItem.setOrderId(orderId);
                        //支付状态
                        orderItem.setStatus(getPayStatus.getStatus());
                        mallShopService.editMallOrderItem(orderItem);
                    }




                }
            }else {
                String commodityIds = cartStr.substring( cartStr.indexOf("[")+1,cartStr.lastIndexOf("]"));
                if (commodityIds.contains(";")){
                    String[] kvs = commodityIds.split(";");
                    for (String kv:kvs
                         ) {
                        String[] kav = kv.split(":");
                        orderItem = addOrderItem(kav[0], Integer.parseInt(kav[1]),addressId);
                        orderItem.setAddressId(addressId);
                        //      消费者id
                        orderItem.setConsumerId(appUser.getId());
                        orderItem.setStatus(getPayStatus.getStatus());
                        orderItem.setOrderId(orderId);
                        orderItem.getShopId();

                        mallShopService.editMallOrderItem(orderItem);
                    }
                }else {
                    String[] split = commodityIds.split(":");
                    orderItem = addOrderItem(split[0], Integer.parseInt(split[1]),addressId);
                    orderItem.setAddressId(addressId);
                    //      消费者id
                    orderItem.setConsumerId(appUser.getId());
                    orderItem.setStatus(getPayStatus.getStatus());
                    orderItem.setOrderId(orderId);
                    mallShopService.editMallOrderItem(orderItem);
                }


            }
        }catch (Exception e){
            e.printStackTrace();
            returnResult.setMessage("cartStr格式解析异常");
            return  returnResult;
        }
        returnResult.setMessage("订单生成成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }
    /**
     * 生成订单项
     * @param commodityId
     */
    public OrderItem addOrderItem(String commodityId,int commodityNum,String addressId){
        OrderItem orderItem = new OrderItem();


        //获取规格
        Specification specification = mallShopService.getSpecificationById(commodityId);
        //获取规格价格
        BigDecimal commodityPrice = specification.getCommodityPrice();

        //获取商铺id
        String shopId = specification.getShopId();
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        //获取运费
        //BigDecimal fare = myMallShop.getFare();

        //该订单项总金额
        //BigDecimal payAmount = commodityPrice.multiply(new BigDecimal(commodityNum)).add(fare);
        //System.out.println(payAmount);
        //生成订单项id
        orderItem.setOrderItemId(UUID.randomUUID().toString().replace("-","").toUpperCase());

        orderItem.setShopId(shopId);
        //规格id
        orderItem.setCommodityId(commodityId);

        //      运费
        orderItem.setFare(getFare(myMallShop.getFeeArea(),addressId));
        //      规格价格
        orderItem.setCommodityPrice(commodityPrice);
        //添加购买商品数量
        orderItem.setCommodityNum(commodityNum);

        return orderItem;


    }


    /**
     *获取我的订单
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOrder")
    @ResponseBody
    @LoginRequired
    public ReturnResult getOrder(@CurrentUser  AppUser appUser,
                                    HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("生成订单项------------->>/mallShop/createOrder");
        getParameterMap(request, response);
        String orderId = request.getParameter("orderId");
        String status = request.getParameter("status");
        List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId,"",status);

        return returnResult;
    }

    /**
     *商户获取所有订单
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOrderByShopId")
    @ResponseBody
    @LoginRequired
    public ReturnResult getOrderByShopId(@CurrentUser  AppUser appUser,
                                  HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();
        log.info("商户获取所有订单------------->>/mallShop/getOrderByShopId");
        getParameterMap(request, response);
        //String orderId = request.getParameter("orderId");
        String status = request.getParameter("status");
        //获取我的商铺id
        MallShop mallShop = mallShopService.myMallShopInfo(appUser.getId());

        String shopId = mallShop.getShopId();
        //获取商铺订单列表
        List<String> orderByShopId = mallShopService.getOrderToItem(shopId,"",status);
        List<ReturnOrder> returnOrders = new ArrayList<>();
        if (StringUtils.isEmpty(orderByShopId)){
            returnResult.setMessage("暂无订单！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(orderByShopId);
            return returnResult;
        }else {

            for (String orderId:orderByShopId
                 ) {
                ReturnOrder returnOrder =new ReturnOrder();
                //根据订单id获取订单项
                List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId,shopId,status);
                List<Specification> commodities = new ArrayList<>();
                String  payAmount ="0";
                for (OrderItem orderItem:mallOrderItems
                     ) {
                    Specification specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                    //设置规格价格
                    specificationById.setCommodityPrice(orderItem.getCommodityPrice());
                    //设置规格购买数量
                    specificationById.setCommodityNum(orderItem.getCommodityNum());
                    payAmount = orderItem.getCommodityPrice().multiply(BigDecimal.valueOf(orderItem.getCommodityNum())).toString();
                    commodities.add(specificationById);
                }
                //去订单号
                Order order = payService.getOrderId(mallOrderItems.get(0).getOrderId());
                AppUser appUserMsg = loginService.getAppUserMsg("", "",  mallOrderItems.get(0).getConsumerId());
                //消费者名
                returnOrder.setConsumerName(appUserMsg.getNickName());
                returnOrder.setConsumerPhone(appUserMsg.getPhone());
                returnOrder.setOrderNo(order.getOrderNo());
                returnOrder.setOrderId(order.getId());
                returnOrder.setCommodities(commodities);
                returnOrder.setPayAmount(new BigDecimal(payAmount).add(mallOrderItems.get(0).getFare()));
                returnOrder.setFare(mallOrderItems.get(0).getFare());
                //下单时间
                returnOrder.setCreateTime(mallOrderItems.get(0).getCreateTime());
                //支付类型
                returnOrder.setTradeType(order.getTradeType());

                returnOrder.setStatus(mallOrderItems.get(0).getStatus());
                returnOrders.add(returnOrder);
            }
        }
        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(returnOrders);
        return returnResult;


    }


    /**
     *商户 获取 订单详情
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
        log.info("商户 获取 订单详情------------->>/mallShop/getOrderDetailByOrderId");
        getParameterMap(request, response);
        String orderId = request.getParameter("orderId");
        Order order = payService.getOrderId(orderId);
        if (StringUtils.isNull(order)){
            returnResult.setMessage("查无该订单！");
            return returnResult;
        }
        //获取商铺订单列表
        ReturnOrderDetail returnOrderDetail=new  ReturnOrderDetail();

        returnOrderDetail.setOrderId(orderId);
        String createTime = order.getCreateTime();
        System.out.println(createTime);
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
        List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId, "","");
        String addressId = mallOrderItems.get(0).getAddressId();
        MallAddress mallAddress = mallShopService.getMallAddress(addressId);
        returnOrderDetail.setMallAddress(mallAddress);
        for (OrderItem orderItem:mallOrderItems
                ) {
                    Specification specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                    //设置规格价格
                    specificationById.setCommodityPrice(orderItem.getCommodityPrice());
                    //设置规格购买数量
                    specificationById.setCommodityNum(orderItem.getCommodityNum());
                    commodities.add(specificationById);
                }

        returnOrderDetail.setCommodities(commodities);
        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(returnOrderDetail);
        return returnResult;

    }

    /**
     *消费者 获取 订单列表
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
        log.info("消费者 获取 订单列表------------->>/mallShop/getOrderByConsumerId");
        getParameterMap(request, response);

        String status = request.getParameter("status");
        String consumerId = appUser.getId();
        List<Order> scOrder = payService.getSCOrder(appUser.getId(), status);
        List<ReturnOrderDetail> returnOrderDetailList = new ArrayList<>();
        if (StringUtils.isNull(scOrder)){
            returnResult.setMessage("暂无订单！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(scOrder);
            return returnResult;
        }

        for (Order order:scOrder
             ) {
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
            List<OrderItem> orderItems= mallShopService.getMallOrderItem(orderId,"",status);
            Specification specificationById = null;
            for (OrderItem orderItem:orderItems
                 ) {
                specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                //设置规格购买数量
                specificationById.setCommodityNum(orderItem.getCommodityNum());
                //设置规格价格
                specificationById.setCommodityPrice(orderItem.getCommodityPrice());
                commodities.add(specificationById);
            }

            returnOrderDetail.setOrderId(order.getId());
            returnOrderDetail.setOrderNo(order.getOrderNo());

            returnOrderDetail.setPayAmount(order.getMoney());
            MallShop myMallShop = mallShopService.getMyMallShop(orderItems.get(0).getShopId());
            returnOrderDetail.setCommodities(commodities);
            returnOrderDetail.setFare(orderItems.get(0).getFare());
            returnOrderDetail.setTradeType(order.getTradeType());
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
     * 临时订单（最新）
     * @param cartStr
     * @param addressId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "temporaryOrder")
    @ResponseBody
    public ReturnResult temporaryOrder(String cartStr,String addressId,
                                          HttpServletRequest request, HttpServletResponse response) {

        ReturnResult returnResult = new ReturnResult();
        log.info("临时订单（最新）------------->>/mallShop/temporaryOrder");
        getParameterMap(request, response);
        List<ResultCart> resultCarts = new ArrayList<>();

        if (cartStr.contains("-")) {

            String[] cartStrings = cartStr.split("-");
            for (String cartString:cartStrings
                 ) {
                ResultCart resultCart = getResultCart(cartString, addressId);
                if (StringUtils.isNull(resultCart)){
                    returnResult.setMessage("数据格式错误！");
                    return returnResult;
                }
                resultCarts.add(resultCart);
            }

        }else {
            ResultCart resultCart = getResultCart(cartStr,addressId);
            if (StringUtils.isNull(resultCart)){
                returnResult.setMessage("数据格式错误！");
                return returnResult;
            }
            resultCarts.add(resultCart);
        }

        returnResult.setMessage("返回成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(resultCarts);
        return returnResult;
    }

    /**
     * 临时订单（最新）获取返回ResultCart结果
     *
     * @param cartStr  传入数据格式如下
     * A0E34543A9414EFDBEB63B6BDDB8156
     *[5FF99665F69C4CE7B33669876395BB7C:1;F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]
     * @param addressId
     * 地址id
     * @return
     */
    public ResultCart getResultCart(String cartStr,String addressId){
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
        BigDecimal getFare =  getFare(feeArea,addressId);
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
                resultCart.setPayAmount(new BigDecimal(payTotal).add(resultCart.getFare()));
            }else {
                String commodityId = commodityIds.split(":")[0];
                String commodityNum = commodityIds.split(":")[1];
                Specification specificationById = mallShopService.getSpecificationById(commodityId);
                specificationById.setCommodityNum(Integer.parseInt(commodityNum));
                commodities.add(specificationById);
                BigDecimal payTotal = specificationById.getCommodityPrice().multiply(new BigDecimal(commodityNum)).add(resultCart.getFare());
                resultCart.setPayAmount(payTotal);
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
    public BigDecimal getFare(String feeArea,String addressId){

        //如果收费区域为空 或是地址为空 ，使用商家设置的运费
        if (StringUtils.isEmpty(feeArea) || StringUtils.isEmpty(addressId)){
            return new BigDecimal(0);
        }
        MallAddress mallAddress = mallShopService.getMallAddress(addressId);
        String specificAddr = mallAddress.getSpecificAddr();
        try{
            String substring = specificAddr.substring(0, specificAddr.indexOf("-"));
            if (feeArea.contains(substring)){
                if (feeArea.contains(";")){
                    String[] split = feeArea.split(";");
                    for (String s:split
                    ) {
                        if (s.contains(substring)){
                            String fare = s.split(":")[1];
                            System.out.println("匹配成功！-"+fare);
                            return new BigDecimal(fare);
                        }
                    }
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
     * @param addressId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMyAddress")
    @ResponseBody
    @LoginRequired
    public ReturnResult getMyAddress(@CurrentUser  AppUser appUser,String addressId,
                                    HttpServletRequest request, HttpServletResponse response){

        ReturnResult returnResult = new ReturnResult();

        log.info("获取我的收货地址------------->>/mallShop/getMyAddress");
        getParameterMap(request, response);
        if (StringUtils.isNotEmpty(addressId)){
            MallAddress mallAddress = mallShopService.getMallAddress(addressId);
            returnResult.setResult(mallAddress);
        }else {
            List<MallAddress> mallAddrByUserId = mallShopService.getMallAddrByUserId(appUser.getId());
            returnResult.setResult(mallAddrByUserId);
        }
        returnResult.setMessage("返回成功");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }


    /**
     *
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
        log.info("获取我的收货地址------------->>/mallShop/editMyAddress");
        getParameterMap(request, response);
        if (StringUtils.isEmpty(mallAddress.getAddressId())){
            mallAddress.setAddressId(UUID.randomUUID().toString().replace("-","").toUpperCase());
            returnResult.setMessage("添加成功");

        }else {
            returnResult.setMessage("修改成功");
        }
        mallAddress.setUserId(appUser.getId());
        mallShopService.editMallAddr(mallAddress);
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;

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
