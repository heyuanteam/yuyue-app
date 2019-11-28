package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Description:定义ItemRepository 接口
 * @Param:
 * 	Item:为实体类
 * 	Long:为Item实体类中主键的数据类型
 * @Author: https://blog.csdn.net/chen_2890
 * @Date: 2018/9/29 0:50
 */
public interface ItemRepository extends ElasticsearchRepository<Item,Long> {

}
