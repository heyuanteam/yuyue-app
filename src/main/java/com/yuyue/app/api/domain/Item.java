package com.yuyue.app.api.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "item",type = "docs", shards = 1, replicas = 0)
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * @Description: @Id注解必须是springframework包下的
     * org.springframework.data.annotation.Id
     *@Author: https://blog.csdn.net/chen_2890
     */
    @Id
    private Long id;

    @Field(type = FieldType.String, analyzer = "ik_max_word")
    private String title; //标题

    @Field(type = FieldType.String)
    private String category;// 分类

    @Field(type = FieldType.String)
    private String brand; // 品牌

    @Field(type = FieldType.Double)
    private Double price; // 价格

    @Field(type = FieldType.String)
    private String images; // 图片地址

}
