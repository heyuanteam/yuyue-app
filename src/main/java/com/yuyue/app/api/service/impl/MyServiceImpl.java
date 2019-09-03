package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Advertisement;
import com.yuyue.app.api.domain.Feedback;
import com.yuyue.app.api.domain.Order;
import com.yuyue.app.api.mapper.AdvertisementMapper;
import com.yuyue.app.api.domain.ShowName;
import com.yuyue.app.api.mapper.FeedbackMapper;
import com.yuyue.app.api.mapper.PayMapper;
import com.yuyue.app.api.mapper.ShowNameMapper;
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
}
