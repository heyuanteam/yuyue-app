package com.yuyue.app.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;

public class SimpleTest {
    @Autowired
    private RedisTemplate redisTemplate;

    public static void main(String[] args) {

        String a="0.05";
        String b="0.1";
        BigDecimal bigDecimal = new BigDecimal(a).multiply(new BigDecimal(b)).setScale(2,BigDecimal.ROUND_HALF_UP);
        System.out.println(bigDecimal);
        System.out.println((int)5/3);

    }

}
