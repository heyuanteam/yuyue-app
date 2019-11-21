package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.OrderItem;
import com.yuyue.app.api.domain.OrderItemVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallOrderItemMapper extends MyBaseMapper<OrderItem> {



    List<OrderItem> getMallOrderItem(@Param(value = "orderId") String orderId,
                                     @Param(value = "shopId") String shopId,@Param(value = "status") String status);

    OrderItemVo getMallOrderItemById(@Param(value = "orderItemId") String orderItemId);

    @Select("SELECT DISTINCT shop_id FROM yuyue_mall_order_item  \n" +
            "        WHERE 1=1 and order_id = #{orderId}")
    List<String> getShopIds(@Param(value = "orderId") String orderId);

    List<String> getOrderToItem(@Param(value = "shopId") String shopId,@Param(value = "consumerId") String consumerId,
                                  @Param(value = "status") String status);

    String getNoPayOrderItem(@Param(value = "merchantId") String merchantId);

    @Transactional
    @Insert("REPLACE INTO yuyue_mall_order_item (order_item_id,order_id,shop_id, \n" +
            "address_id,commodity_id,consumer_id,merchant_id,fare,commodity_price,shop_income,commodity_num,status) \n" +
            "VALUES \n" +
            "(#{orderItemId},#{orderId},#{shopId},#{addressId},#{commodityId},#{consumerId}, #{merchantId},\n" +
            "#{fare},#{commodityPrice},#{shopIncome},#{commodityNum},#{status})")
    void editMallOrderItem(OrderItem orderItem);

//    @Transactional
//    @Update("update yuyue_mall_order_item  set status= #{status} WHERE order_id = #{orderId}")
//    void updateOrderItemsStatus(@Param(value = "orderId") String orderId,@Param(value = "status") String status);


    @Transactional
    @Update("update yuyue_mall_order_item  set status= #{status} WHERE order_item_id = #{orderItemId}")
    void updateOrderItemsStatus(@Param(value = "orderItemId") String orderItemId,@Param(value = "status") String status);

    List<OrderItemVo> getMerchantOrder(@Param(value = "merchantId")String merchantId);
}
