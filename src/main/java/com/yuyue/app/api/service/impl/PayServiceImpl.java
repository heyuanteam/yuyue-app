package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Order;
import com.yuyue.app.api.mapper.PayMapper;
import com.yuyue.app.api.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayMapper payMapper;

    @Override
    public void createOrder(Order order) { payMapper.createOrder(order); }

    @Override
    public Order getOrderId(String orderId) {return payMapper.getOrderId(orderId); }

    @Override
    public void updateStatus(String id,String status) {payMapper.updateStatus(id,status);}

    @Override
    public void updateOrderStatus(String responseCode, String responseMessage, String status,String orderno) {
        payMapper.updateOrderStatus(responseCode,responseMessage,status,orderno);
    }
}
