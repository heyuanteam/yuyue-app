package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartMapper extends MyBaseMapper<Cart> {

    @Select("SELECT * from yuyue_mall_cart where cart_id = #{cartId} \n" +
            "or  consumer_id = #{consumerId} ")
    List<Cart> getCarts(@Param(value = "cartId") String cartId,@Param(value = "consumerId") String consumerId);


    @Select("SELECT * from yuyue_mall_cart where  commodity_id = #{commodityId} " +
            "and consumer_id = #{consumerId}    LIMIT 1" )
    Cart getCart(@Param(value = "commodityId") String commodityId,@Param(value = "consumerId") String consumerId);

    @Transactional
    @Insert("REPLACE INTO yuyue_mall_cart " +
            "(cart_id,consumer_id,commodity_id,shop_id,commodity_num) \n" +
            "VALUES \n" +
            "(#{cartId},#{consumerId},#{commodityId},#{shopId},#{commodityNum});")
    void addCart (Cart cart);

    @Delete("DELETE FROM yuyue_mall_cart where cart_id = #{cartId} or shop_id = #{shopId} ")
    void deleteCart (Cart cart);
}
