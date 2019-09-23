package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.ChangeMoney;
import com.yuyue.app.api.domain.Gift;
import com.yuyue.app.api.domain.Order;
import com.yuyue.app.api.domain.OutMoney;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PayService {
    void createOrder(Order order);

    Order getOrderId(String orderId);

    void updateStatus(String id,String status);

    void updateOrderStatus(String responseCode, String responseMessage, String status,String orderno);

    void updateTotal(String merchantId, BigDecimal money);

    void createOut(OutMoney outMoney);

    void updateOutStatus(String code, String msg, String s, String outNo);

    void updateOutIncome(String merchantId, BigDecimal money);

    List<OutMoney> getOutMoneyList(String id);

    void sendMoney(String id, BigDecimal money);

    List<Gift> getGiftList();

    Gift getGift(String id);

    void addIncome(String id, BigDecimal setScale);

    void createShouMoney(ChangeMoney changeMoney);
}
