package com.yuyue.app;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//开启异步调用方法
@EnableAsync
@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

//    上线放开
//    public class AppApplication extends SpringBootServletInitializer {
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        // 注意这里要指向原先用main方法执行的Application启动类
//        return builder.sources(AppApplication.class);
//    }

    //Page报错处理
    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    // 为null处理
//    @Bean
//    public HttpMessageConverters fastJsonConverters() {
//        FastJsonHttpMessageConverter4 fastConverter = new FastJsonHttpMessageConverter4();
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.IgnoreNonFieldGetter,
//                SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        List supportedMediaTypes = new ArrayList();
//        supportedMediaTypes.add(new MediaType("text", "json", Charset.forName("utf8")));
//        supportedMediaTypes.add(new MediaType("application", "json", Charset.forName("utf8")));
//        fastConverter.setSupportedMediaTypes(supportedMediaTypes);
//        HttpMessageConverter<?> converter = fastConverter;
//        return new HttpMessageConverters(converter);
//    }

}