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
    public List<Order> getMoneyList(String id) { return payMapper.getMoneyList(id); }

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
     * @param spokesPersonId
     * @return
     */
    @Override
    public List<Advertisement> getCommodityInfo(String merchantId, String spokesPersonId) {
        return commodityMapper.getCommodityInfo(merchantId,spokesPersonId);
    }

    @Override
    public List<AdPrice> getAdvertisementFeeInfo(String priceId) {
        return adPriceMapper.getAdvertisementFeeInfo(priceId);
    }

    @Override
    public Feedback getFeedback(String details) { return feedbackMapper.getFeedback(details); }

    @Override
    public Advertisement findAdvertisement(String agencyCode) { return advertisementMapper.findAdvertisement(agencyCode);}

    @Override
    public ShowName findShowName(String description, String phone) { return showNameMapper.findShowName(description,phone); }

    @Override
    public void insertShowName(String id, String userId, String teamName, String description, String size,
                               String categoryId, String address, String phone, String cardZUrl, String cardFUrl,
                               String videoAddress, String mail, String weChat) {
        showNameMapper.insertShowName(id,userId,teamName,description,size,categoryId,address,phone,cardZUrl,
                cardFUrl,videoAddress,mail,weChat);
    }


}
