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
    public void insertShowName(ShowName showName) { showNameMapper.insertShowName(showName); }

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


}
