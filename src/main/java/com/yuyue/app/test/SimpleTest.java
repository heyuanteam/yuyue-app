package com.yuyue.app.test;

import org.apache.commons.collections.list.TreeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class SimpleTest {
    @Autowired
    private RedisTemplate redisTemplate;

    public static void main(String[] args) {

        List<String> list =new TreeList();
        list.add("aaaa");
        list.add("aaaa");
        list.add("aaaa");
        list.add("bbbb");
        list.add("bbbb");
        for (String s:list
             ) {
            System.out.println(s);
        }

     }

}
