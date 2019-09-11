package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.*;

import java.util.List;

public interface MyService {
    void insertFeedback(Feedback feedback);

    List<Order> getMoneyList(String id);

    /**
     * 插入广告推广信息
     */
    void addAdvertisemenInfo(Advertisement advertisement);


    Advertisement getAdvertisementInfo(String userId);

    void insertShowName(ShowName showName);

    ShowName getShowInfo(String id);

    /**
     *商家上传广告申请
     * @param commodity
     */
    void commodityToSpread(Commodity commodity);

    /**
     * 公告代言人或者商家id 查询 广告
     * @param merchantId
     * @param spokesPersonId
     * @return
     */
    List<Advertisement> getCommodityInfo(String merchantId, String spokesPersonId);

    /**
     * 获取广告费用信息
     * @param
     * @return
     */
    List<AdPrice> getAdvertisementFeeInfo(String priceId);
}
