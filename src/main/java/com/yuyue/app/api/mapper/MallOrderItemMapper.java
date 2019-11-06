package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallOrderItemMapper extends MyBaseMapper<OrderItem> {



    @Select("SELECT * FROM yuyue_mall_order_item WHERE order_id = #{orderId} and status = '10A'")
    List<OrderItem> getMallOrderItem(@Param(value = "orderId") String orderId);



    List<OrderItem> getMallOrderItem12(@Param(value = "orderId") String orderId);



    @Transactional
    @Insert("REPLACE INTO yuyue_mall_order_item (order_item_id,order_id,shop_id,\n" +
            "address_id,commodity_id,consumer_id,fare,commodity_price,commodity_num,status) \n" +
            "VALUES \n" +
            "(#{orderItemId},#{orderId},#{shopId},#{addressId},#{commodityId},#{consumerId}, \n" +
            "#{fare},#{commodityPrice},#{commodityNum},#{status})")
    void editMallOrderItem(OrderItem orderItem);
}
