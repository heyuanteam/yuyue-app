package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.service.MallShopService;
import com.yuyue.app.api.service.MyService;
import com.yuyue.app.api.service.PayService;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/mallShop" , produces = "application/json; charset=UTF-8")
@Slf4j
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
        log.info("查询我的商铺-------------->>/mallShop/getAllMallShop");
        getParameterMap(request, response);
        String myArea = request.getParameter("myArea");
        String page = request.getParameter("page");
        String content = request.getParameter("content");
        if(StringUtils.isEmpty(myArea)){
            returnResult.setMessage("定位地址不能为空！");
            return returnResult;
        }
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        PageHelper.startPage(Integer.parseInt(page), 10);
        List<MallShop> allMallShop = mallShopService.getAllMallShop(myArea,content);
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(allMallShop);
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
     *
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
        log.info("修改规格------------->>/mallShop/updateSpecification");

        getParameterMap(request, response);
        List<Cart> carts = mallShopService.getCarts("",appUser.getId());
        if (StringUtils.isEmpty(carts)){
            returnResult.setMessage("暂无商品！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(carts);
            return returnResult;
        }
        HashMap<String,List<Cart>> hashMap =  new HashMap<>();
        for (Cart cart : carts
             ) {
            MallShop myMallShop = mallShopService.getMyMallShop(cart.getShopId());
            if (hashMap.containsKey(myMallShop.getCommodityName())){
                List<Cart> addCarts = hashMap.get(myMallShop.getCommodityName());
                addCarts.add(cart);
                hashMap.put(myMallShop.getCommodityName(),addCarts);
            }else {
                List<Cart> newCarts =  new LinkedList<>();
                newCarts.add(cart);
                hashMap.put(myMallShop.getCommodityName(),newCarts);
            }
        }
        returnResult.setMessage("查询成功！");
        returnResult.setStatus(Boolean.TRUE);
        returnResult.setResult(hashMap);
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
        log.info("修改规格------------->>/mallShop/updateSpecification");

        getParameterMap(request, response);
        Cart cart = new Cart();


        String commodityId = request.getParameter("commodityId");
        String shopId = request.getParameter("shopId");
        String commodityName = request.getParameter("commodityName");
        String commodityPrice = request.getParameter("commodityPrice");
        String commodityNum = request.getParameter("commodityNum");
        String commoditySize = request.getParameter("commoditySize");

        cart.setConsumerId(appUser.getId());
        java.util.regex.Matcher match=pattern.matcher(commodityPrice);
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
        if (StringUtils.isEmpty(commodityName)){
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
        }

        if(commodityNum.matches("[0-9]+")){
            String cartId = request.getParameter("cartId");
            int numAfter = Integer.parseInt(commodityNum);
            if (StringUtils.isEmpty(cartId)){
                 if (StringUtils.isNull(mallShopService.getCart(commodityId, appUser.getId()))){
                     cartId = UUID.randomUUID().toString().replace("-","").toUpperCase();
                 }else {
                     int numBefore= mallShopService.getCart(commodityId, appUser.getId()).getCommodityNum();
                     numAfter = numBefore + numAfter;
                     cartId = mallShopService.getCart(commodityId, appUser.getId()).getCartId();
                 }

            }else {
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

        cart.setCommoditySize(commoditySize);
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
        log.info("修改规格------------->>/mallShop/updateSpecification");

        getParameterMap(request, response);
        cart.setConsumerId(appUser.getId());

        mallShopService.deleteCart(cart);
        returnResult.setMessage("删除成功！");
        returnResult.setStatus(Boolean.TRUE);
        return returnResult;
    }
}
