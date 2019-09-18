package com.yuyue.app.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SimpleTest {
    @Autowired
    private RedisTemplate redisTemplate;

    public static void main(String[] args) {

        String a="0.05";
        String b="0.1";
        BigDecimal bigDecimal = new BigDecimal(a).multiply(new BigDecimal(b)).setScale(2,BigDecimal.ROUND_HALF_UP);
        System.out.println(bigDecimal);
        System.out.println((int)5/3);

        Date javaUtilCurrentTime = new Date();
        System.out.println(javaUtilCurrentTime);
        String time1="2019-11-06 19:00:00";
        String time2="2019-11-06 21:00:00";
        String[] split1 = time1.split(" ");
        String[] split2 = time2.split(" ");
        time1.split(" ");
        int res=time1.compareTo(time2);
        System.out.println(res);
        int s1 = Integer.parseInt(split1[1].split(":")[0]);
        int s2 = Integer.parseInt(split2[1].split(":")[0]);
        System.out.println(s1-s2);
        System.out.println();
        String format = new SimpleDateFormat("HH").format(new Date());
        System.out.println(format);
        System.out.println("_______"+split2[1]+"_______"+split1[1]);

    }

}
