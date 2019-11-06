package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.*;

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

/*---------------------------------评价---------------------------------*/
    List<MallComment> getMallComments(String shopId);


    MallComment getMallComment( String shopId,String consumerId);


    double getScore( String shopId);


    void addMallComment(MallComment mallComment);


/*---------------------------------订单---------------------------------*/


    List<OrderItem> getMallOrderItem(String orderId);


    void editMallOrderItem(OrderItem orderItem);

/*---------------------------------地址---------------------------------*/
    List<MallAddress> getMallAddrByUserId(String userId);

    MallAddress getMallAddress(String addressId);

    void editMallAddr(MallAddress mallAddress);

    void deleteMallAddr(String addressId);



}
