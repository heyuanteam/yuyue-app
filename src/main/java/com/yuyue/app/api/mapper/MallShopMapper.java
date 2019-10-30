package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.MallShop;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallShopMapper extends MyBaseMapper<MallShop> {

    List<MallShop> getAllMallShop(@Param(value = "myArea") String myArea);

    MallShop getMyMallShop(@Param(value = "shopId") String shopId);

    @Transactional
    void insertMyMallShop(MallShop mallShop);

    @Transactional
    void updateMyMallShopInfo(MallShop mallShop);
}
