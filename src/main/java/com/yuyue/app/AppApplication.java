package com.yuyue.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

//开启异步调用方法
@EnableAsync
@SpringBootApplication
//public class AppApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(AppApplication.class, args);
//
//    }

//    上线放开
    public class AppApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(AppApplication.class);
    }

}