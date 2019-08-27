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

    @Scheduled(cron = "0 0/5 * * * *")
    public void work() {
        System.out.println(dateFormat.format(new Date())+"我被执行了------------------");
    }
}
