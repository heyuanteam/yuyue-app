package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.ChangeMoney;
import com.yuyue.app.api.domain.Gift;
import com.yuyue.app.api.domain.Order;
import com.yuyue.app.api.domain.OutMoney;
import com.yuyue.app.api.mapper.PayMapper;
import com.yuyue.app.api.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service(value = "PayService")
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

    @Override
    public void updateTotal(String merchantId, BigDecimal money) { payMapper.updateTotal(merchantId,money); }

    @Override
    public void createOut(OutMoney outMoney) { payMapper.createOut(outMoney); }

    @Override
    public void updateOutStatus(String responseCode, String responseMessage, String status, String outNo) {
        payMapper.updateOutStatus(responseCode,responseMessage,status,outNo);
    }

    @Override
    public void updateOutIncome(String merchantId, BigDecimal money) {
        payMapper.updateOutIncome(merchantId,money);
    }

    @Override
    public List<OutMoney> getOutMoneyList(String id) { return payMapper.getOutMoneyList(id); }

    @Override
    public List<Gift> getGiftList() { return payMapper.getGiftList();}

    @Override
    public Gift getGift(String id) { return payMapper.getGift(id);}

    @Override
    public void createShouMoney(ChangeMoney changeMoney) {  payMapper.createShouMoney(changeMoney); }

}
