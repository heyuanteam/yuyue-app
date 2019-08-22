package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
public interface PayMapper extends MyBaseMapper<Order> {

    @Transactional
    @Insert("insert into yuyue_order (id,orderNo,tradeType,money,modle,status,statusCode,merchantId)  values  " +
            "(#{id},#{orderNo},#{tradeType},#{money},#{modle},#{status},#{statusCode},#{merchantId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void createOrder(Order order);

    @Select("SELECT * FROM yuyue_order b WHERE b.id = #{orderId} ")
    Order getOrderId(String orderId);

    @Transactional
    @Update("UPDATE yuyue_order b SET b.`status` = #{status} WHERE b.id = #{id} ")
    void updateStatus( @Param("id")String id,@Param("status") String status);

    @Transactional
    @Update("UPDATE yuyue_order b SET b.`status`=#{status},b.responseCode=#{responseCode},b.responseMessage=#{responseMessage} " +
            "WHERE b.orderNo = #{orderNo} ")
    void updateOrderStatus(@Param("responseCode") String responseCode,@Param("responseMessage") String responseMessage,
                           @Param("status") String status, @Param("orderNo") String orderNo);
}
