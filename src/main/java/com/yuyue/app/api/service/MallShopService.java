package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Cart;
import com.yuyue.app.api.domain.MallShop;
import com.yuyue.app.api.domain.ShopImage;
import com.yuyue.app.api.domain.Specification;

import java.util.List;

public interface MallShopService {


/*---------------------------------商铺接口---------------------------------*/
    MallShop getMyMallShop(String shopId);

    List<MallShop> getAllMallShop(String myArea,String content);

    void insertMyMallShop(MallShop mallShop);

    void updateMyMallShopInfo(MallShop mallShop);

    ShopImage getShopImage(String shopId);

    void insertShopImage(ShopImage shopImage);

    void deleteShopImage(String imagePath);

/*---------------------------------商品(规格)接口---------------------------------*/
    List<Specification> getSpecification(String shopId);

    Specification getSpecificationById(String specificationId);

    void insertSpecification(Specification specification);

    void deleteSpecification(String specificationId);

    void updateSpecification(Specification specification);

/*---------------------------------购物车---------------------------------*/
    List<Cart> getCarts(String cartId,String  consumerId);

    Cart getCart(String commodityId,String consumerId);

    void editCart (Cart cart);

    void deleteCart (Cart cart);

}
