package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.mapper.*;
import com.yuyue.app.api.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "MyService")
public class MyServiceImpl implements MyService {

    @Autowired
    private FeedbackMapper feedbackMapper;
    @Autowired
    private PayMapper payMapper;
    @Autowired
    private ShowNameMapper showNameMapper;
    @Autowired
    private AdvertisementMapper advertisementMapper;
    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private AdPriceMapper adPriceMapper;

    @Override
    public void insertFeedback(Feedback feedback) { feedbackMapper.insertFeedback(feedback); }

    @Override
    public List<Order> getMoneyList(String id,int begin, int size) { return payMapper.getMoneyList(id,begin,size); }

    @Override
    public void addAdvertisemenInfo(Advertisement advertisement) {
        advertisementMapper.addAdvertisemenInfo(advertisement);
    }

    @Override
    public Advertisement getAdvertisementInfo(String userId) {
        return advertisementMapper.getAdvertisementInfo(userId);
    }

    @Override
    public ShowName getShowInfo(String id){
        return showNameMapper.getShowInfo(id);
    }
    /**
     *商家上传广告申请
     * @param commodity
     */
    @Override
    public void commodityToSpread(Commodity commodity) {
         commodityMapper.commodityToSpread(commodity);
    }
    /**
     * 公告代言人或者商家id 查询 广告
     * @param merchantId
     * @param videoId
     * @return
     */
    @Override
    public List<Commodity> getCommodityInfo(String merchantId, String videoId,String commodityId,int begin,int limit) {
        return commodityMapper.getCommodityInfo(merchantId,videoId,commodityId,begin,limit);
    }
    public void updateCommodityStatus(String commodityId,String status){
        commodityMapper.updateCommodityStatus(commodityId,status);
    }
    @Override
    public List<AdPrice> getAdvertisementFeeInfo(String priceId,String isFirstTime) {

        return adPriceMapper.getAdvertisementFeeInfo(priceId,isFirstTime);
    }

    @Override
    public void insertShowName(String id, String userId, String teamName, String description, String size,
                               String categoryId, String address, String phone,
                               String videoAddress,String imageAddress,  String mail, String weChat) {
        showNameMapper.insertShowName(id,userId,teamName,description,size,categoryId,address,phone,videoAddress,imageAddress,mail,weChat);
    }

    @Override
    public List<ChangeMoneyVo> changeMoneyList(String userId,String videoId,String tradeType, int begin, int size) { return payMapper.changeMoneyList(userId,videoId,tradeType,begin,size); }

    @Override
    public Feedback getFeedback(String details, String contact) { return feedbackMapper.getFeedback(details,contact); }

    @Override
    public ShowName findShowName(String description, String phone, String teamName) { return showNameMapper.findShowName(description,phone,teamName); }

    @Override
    public Advertisement findAdvertisement(String agencyCode, String produceAddr, String phone) { return advertisementMapper.findAdvertisement(agencyCode,produceAddr,phone);}

    @Override
    public ChangeMoney getChangeMoney(String orderId,String orderItemId) {
        return payMapper.getChangeMoney(orderId,orderItemId); }

    @Override
    public String getMoneyStatus(String orderId) {
        return payMapper.getMoneyStatus(orderId); }

}
