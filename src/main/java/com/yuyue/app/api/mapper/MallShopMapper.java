package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Distance;
import com.yuyue.app.api.domain.MallShop;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallShopMapper extends MyBaseMapper<MallShop> {

    @Select("SELECT shop_id FROM yuyue_mall_shop WHERE order_id = #{orderId} limit 1  ")
    String getMyMallShopByOrderId(@Param(value = "orderId") String orderId);

    @Update("UPDATE yuyue_mall_shop SET `status` = '10B' WHERE shop_id =  #{shopId}")
    void updateMallShop(String shopId);


    List<MallShop> getMallShopByVideoId(@Param(value = "videoId")String videoId);

    //获取商铺列表
    List<MallShop> getAllMallShop(@Param(value = "myArea") String myArea,@Param(value = "content") String content);

    //获取我的商铺包括图片
    MallShop getMyMallShop(@Param(value = "shopId") String shopId);

    //我的广告
    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId} AND status != '10A' ORDER BY   `status` ASC,create_time DESC")
    List<MallShop> getMyMallShops(@Param(value = "merchantId")String merchantId);

//    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId} LIMIT 1 ")
//    MallShop myMallShopInfo(@Param(value = "merchantId") String merchantId);

    //只获取信息不含其他
    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId}  ")
    List<MallShop> myMallShopInfo(@Param(value = "merchantId") String merchantId);

    @Transactional
    void insertMyMallShop(MallShop mallShop);

    @Transactional
    void updateMyMallShopInfo(MallShop mallShop);

    @Transactional
    @Update("update yuyue_mall_shop set `business_status` = #{businessStatus} where shop_id = #{shopId} ")
    void updateMyMallShopStatus(@Param(value = "businessStatus")String businessStatus,@Param(value = "shopId") String shopId);

    List<Distance> getDistanceAll(@Param(value = "id")String id);
}
