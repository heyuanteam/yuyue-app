package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.*;

import java.util.List;

public interface MyService {
    void insertFeedback(Feedback feedback);

    List<Order> getMoneyList(String id, int begin, int size);

    /**
     * 插入广告推广信息
     */
    void addAdvertisemenInfo(Advertisement advertisement);


    Advertisement getAdvertisementInfo(String userId);

    ShowName getShowInfo(String id);

    /**
     *商家上传广告申请
     * @param commodity
     */
    void commodityToSpread(Commodity commodity);

    /**
     * 视频Id或者商家id 查询 广告
     * @param merchantId
     * @param videoId
     * @return
     */
    List<Commodity> getCommodityInfo(String merchantId, String videoId,String commodityId);


    void updateCommodityStatus(String commodityId,String status);
    /**
     * 获取广告费用信息
     * @param
     * @return
     */
    List<AdPrice> getAdvertisementFeeInfo(String priceId);

    void insertShowName(String id, String userId, String teamName, String description, String size,
                        String categoryId, String address, String phone, String cardZUrl, String cardFUrl,
                        String videoAddress, String mail, String weChat);

    List<ChangeMoneyVo> changeMoneyList(String id, int begin, int size);

    Feedback getFeedback(String details, String contact);

    ShowName findShowName(String description, String phone, String teamName);

    Advertisement findAdvertisement(String agencyCode, String produceAddr, String phone);
}
