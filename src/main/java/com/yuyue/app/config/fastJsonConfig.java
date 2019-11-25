package com.yuyue.app.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class fastJsonConfig extends WebMvcConfigurerAdapter {

    /**
     * 使用阿里 fastjson 作为JSON MessageConverter
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
                //json格式输出
                SerializerFeature.PrettyFormat,
                // 保留map为空的字段
                SerializerFeature.WriteMapNullValue,
                // 将String类型的null转成""形式
                SerializerFeature.WriteNullStringAsEmpty,
                // 将Number类型的null转成0，也可以理解为Integer
                SerializerFeature.WriteNullNumberAsZero,
                // 将List类型的null转成[],而不是“””
                SerializerFeature.WriteNullListAsEmpty,
                // Boolean类型的null转成false
                SerializerFeature.WriteNullBooleanAsFalse,
                // 处理可能循环引用的问题
                SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypeList);
        converters.add(converter);
    }
}