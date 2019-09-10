package com.yuyue.app.task;

import com.yuyue.app.api.controller.SendSmsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private SendSmsController sendSmsController;

    /**
     * 自动提现
     */
    @Scheduled(cron = "0 0/3 * * * *")
    public void outMoney() {
        log.info("提现开始==================================>>>>>>>>>>>");



        log.info("提现结束==================================>>>>>>>>>>>");
    }
}
