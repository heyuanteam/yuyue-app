package com.yuyue.app.api.controller;

import com.github.pagehelper.PageHelper;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.MallShop;
import com.yuyue.app.api.domain.ShopImage;
import com.yuyue.app.api.domain.Specification;
import com.yuyue.app.api.service.MallShopService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/mallShop" , produces = "application/json; charset=UTF-8")
@Slf4j
public class MallShopController extends BaseController{

    @Autowired
    private MallShopService mallShopService;

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
        if(StringUtils.isEmpty(myArea)){
            returnResult.setMessage("定位地址不能为空！");
            return returnResult;
        }
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        PageHelper.startPage(Integer.parseInt(page), 10);
        List<MallShop> allMallShop = mallShopService.getAllMallShop(myArea);
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

        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        if (StringUtils.isNotNull(myMallShop)){
            System.out.println("已添加待审核，请勿重复添加"+myMallShop);
            returnResult.setMessage("已添加待审核，请勿重复添加！");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(myMallShop);
            return returnResult;
        }else {
            MallShop mallShop =new MallShop();
            mallShop.setShopId(shopId);
            mallShop.setMerchantId(user.getId());
            mallShop.setCategory(request.getParameter("commodity"));
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
            // 判断小数点后2位的数字的正则表达式
            java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
            java.util.regex.Matcher match=pattern.matcher(request.getParameter("fare"));
            if(match.matches()==false)
            {
                returnResult.setMessage("价格输入错误！！！");
                return returnResult;
            }
            mallShop.setFare(new BigDecimal(request.getParameter("fare")));
            mallShop.setVideoPath(request.getParameter("remark"));
            mallShop.setBusinessTime(request.getParameter("businessTime"));
            mallShop.setMerchantAddr(request.getParameter("merchantAddr"));
            mallShop.setMerchantPhone(request.getParameter("merchantPhone"));
            mallShop.setServiceArea(request.getParameter("serviceArea"));
            mallShop.setFeeArea(request.getParameter("feeArea"));
            mallShop.setVideoPath(request.getParameter("videoPath"));
            mallShop.setVideoPath(request.getParameter("remark"));
            mallShopService.insertMyMallShop(mallShop);
            System.out.println("添加成功，等待审核"+mallShop);
            returnResult.setMessage("添加成功，等待审核");
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(mallShop);
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
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
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
     * 编辑规格(添加删除)
     * @param commodityId
     * @param shopId
     * @param commodityDetail
     * @param commoditySize
     * @param commodityPrice
     * @param commodityReserve
     * @param imagePath
     * @param status
     * @param user
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
                                          @CurrentUser AppUser user,
                                          HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        getParameterMap(request, response);
        if (StringUtils.isEmpty(commodityId)){

            Specification specification = new Specification();
            specification.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());

            if (StringUtils.isEmpty(commodityPrice)){
                returnResult.setMessage("价格不能为空！");
                return returnResult;
            }
            // 判断小数点后2位的数字的正则表达式
            java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
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
            specification.setShopId(shopId);
            specification.setCommodityDetail(commodityDetail);
            System.out.println(commodityDetail);
            specification.setCommoditySize(commoditySize);
            specification.setImagePath(imagePath);
            System.out.println(specification);
            mallShopService.insertSpecification(specification);
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setResult(specification);
            returnResult.setMessage("规格添加成功！");
            return returnResult;
        }else {
            log.info("删除规格------------->>/mallShop/deleteSpecificationById");
            if (StringUtils.isEmpty(commodityId)){
                returnResult.setMessage("规格id不能为空！！");
                return returnResult;
            }
            mallShopService.deleteSpecification(commodityId);
            returnResult.setStatus(Boolean.TRUE);
            returnResult.setMessage("删除成功！");
            return returnResult;
        }

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
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
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
}
