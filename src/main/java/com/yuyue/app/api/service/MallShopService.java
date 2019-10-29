package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.MallShop;
import com.yuyue.app.api.domain.ShopImage;
import com.yuyue.app.api.domain.Specification;

import java.util.List;

public interface MallShopService {


/*----------------------------商铺接口--------------------------------------*/
    MallShop getMyMallShop(String shopId);

    List<MallShop> getAllMallShop(String myArea);

    void insertMyMallShop(MallShop mallShop);


    ShopImage getShopImage(String shopId);

    void insertShopImage(ShopImage shopImage);
/*----------------------------商品接口-------------————————---------*/

    List<Specification> getSpecification(String shopId);

    void insertSpecification(Specification specification);


}
