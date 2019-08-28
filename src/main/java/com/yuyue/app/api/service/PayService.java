package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Order;

import java.util.Map;

public interface PayService {
    void createOrder(Order order);

    Order getOrderId(String orderId);

    void updateStatus(String id,String status);

    void updateOrderStatus(String responseCode, String responseMessage, String status,String orderno);

    void updateTotal(String merchantId, String money);
}
