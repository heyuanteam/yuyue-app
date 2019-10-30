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

    void updateMyMallShopInfo(MallShop mallShop);

    ShopImage getShopImage(String shopId);

    void insertShopImage(ShopImage shopImage);

    void deleteShopImage(String imagePath);
/*----------------------------商品接口-------------————————---------*/

    List<Specification> getSpecification(String shopId);

    Specification getSpecificationById(String specificationId);

    void insertSpecification(Specification specification);

    void deleteSpecification(String specificationId);

    void updateSpecification(Specification specification);

}
