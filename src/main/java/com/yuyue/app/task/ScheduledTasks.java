package com.yuyue.app.task;

import com.yuyue.app.api.domain.Order;
import com.yuyue.app.api.service.PayService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PayService pyService;

    /**
     * 订单支付超时判断
     */
    @Scheduled(cron = "0 0/25 * * * *")
    public void outTime() {
        log.info("订单支付超时判断开始==================================>>>>>>>>>>>");
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        c.add(Calendar.MINUTE,-30);
        String startTime = dateFormat.format(c.getTime());
        System.out.println("========>>>"+startTime);
        List<Order> list = pyService.findOrderList(startTime);
        if(CollectionUtils.isNotEmpty(list)){
            for (Order order: list) {
                log.info("订单"+order.getOrderNo()+"=====金额："+order.getMoney()+">>>>>>>>>>>已超时");
                pyService.updateOrderStatus("ERROR", "支付超时", "10D", order.getOrderNo());
            }
        }
        log.info("订单支付超时判断结束==================================>>>>>>>>>>>");
    }
}
