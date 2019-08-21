package com.yuyue.app.text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class SimpleText {
    @Autowired
    private RedisTemplate redisTemplate;

    public static void main(String[] args) {

    }
}
