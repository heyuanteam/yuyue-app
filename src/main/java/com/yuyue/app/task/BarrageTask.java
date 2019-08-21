package com.yuyue.app.task;


import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.service.BarrageService;



import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BarrageTask  {


    public static void main(String[] args) {

        final Timer[] timer = {new Timer()};

        timer[0].schedule(new  TimerTask(){

            public BarrageService barrageService;

            @Override
            public void run() {
                System.out.println("即时任务开始");
                List<Barrage> barrages = barrageService.getBarrages("2");
                System.out.println(barrages);
            }
        },1000,1000);



    }


}
