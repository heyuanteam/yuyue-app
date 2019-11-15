package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.MallShop;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallShopMapper extends MyBaseMapper<MallShop> {

    List<MallShop> getAllMallShop(@Param(value = "myArea") String myArea,@Param(value = "content") String content);

    MallShop getMyMallShop(@Param(value = "shopId") String shopId);

    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId}  ")
    List<MallShop> getMyMallShops(@Param(value = "merchantId")String merchantId);

    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId} LIMIT 1 ")
    MallShop myMallShopInfo(@Param(value = "merchantId") String merchantId);

    @Transactional
    void insertMyMallShop(MallShop mallShop);

    @Transactional
    void updateMyMallShopInfo(MallShop mallShop);
}
