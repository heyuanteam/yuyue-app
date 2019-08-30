package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Advertisement;
import com.yuyue.app.api.domain.Feedback;
import com.yuyue.app.api.domain.Order;

import java.util.List;

public interface MyService {
    void insertFeedback(Feedback feedback);

    List<Order> getMoneyList(String id);

    /**
     * 插入广告推广信息
     */
    void addAdvertisemenInfo(Advertisement advertisement);


    Advertisement getAdvertisementInfo(String userId);
}
