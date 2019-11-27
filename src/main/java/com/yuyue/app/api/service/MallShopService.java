package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.*;

import java.util.List;

public interface MallShopService {


/*---------------------------------商铺接口---------------------------------*/
    List<ShopAttention> getShopAttentions(String userId,String shopId);

    void addShopAttention(ShopAttention shopAttention);

    void cancelShopAttention(String userId,String shopId);

    void getMyMallShopByOrderId(String orderId);

    List<MallShop> getMallShopByVideoId(String videoId);

    MallShop getMyMallShop(String shopId);

    List<MallShop> getMyMallShops(String merchantId);

    //MallShop myMallShopInfo(String merchantId);
    //
    List<MallShop> myMallShopInfo(String merchantId);

    List<MallShop> getAllMallShop(String myArea,String content);

    void insertMyMallShop(MallShop mallShop);

    void updateMyMallShopInfo(MallShop mallShop);

    void updateMyMallShopStatus(String businessStatus, String shopId);

    List<ShopImage> getShopImage(String shopId);

    void insertShopImage(ShopImage shopImage);

    void deleteShopImage(String imagePath);

    void deleteImageByShopId(String shopId);

/*---------------------------------商品(规格)接口---------------------------------*/
    //仅获取10B状态下的规格
    List<Specification> getSpecification(String shopId);

    List<Specification> getAllSpecification(String shopId);

    Specification getSpecificationById(String specificationId);

    void insertSpecification(Specification specification);

    void deleteSpecification(String specificationId);

    void updateSpecification(Specification specification);

/*---------------------------------购物车---------------------------------*/
    List<Cart> getCarts(String cartId,String  consumerId);

    Cart getCart(String commodityId,String consumerId);

    void editCart (Cart cart);

    void deleteCart (String cardId , String shopId);

    void deletePayCart (String consumerId ,String commodityId);

/*---------------------------------评价---------------------------------*/
    List<MallComment> getMallComments(String shopId);


    MallComment getMallComment( String shopId,String orderId,String consumerId);


    double getScore( String shopId);


    void addMallComment(MallComment mallComment);


/*---------------------------------订单---------------------------------*/


    List<OrderItem> getMallOrderItem(String orderId,String shopId,String status);


    OrderItemVo getMallOrderItemById(String orderItemId);

    List<String> getOrderToItem(String shopId,String consumerId,String status);


    String getNoPayOrderItem(String merchantId);

    //获取顾客订单
    List<OrderItemVo> getMerchantOrder(String merchantId);


    void editMallOrderItem(OrderItem orderItem);

    //修改该订单下的所有订单项中的支付状态
    //void updateOrderItemsStatus(String orderId,String status);

    //修改单个订单项中的支付状态
    void updateOrderItemsStatus(String orderItemId,String status);

    //减库存及给商家们加钱的方法
    void mallPaySuccess(String orderId);

/*---------------------------------地址---------------------------------*/
    List<MallAddress> getMallAddrByUserId(String userId);

    MallAddress getDefaultAddress(String userId);

    MallAddress getMallAddress(String addressId);

    MallAddress getMallAddressByStatus(String addressId);

    List<MallAddress> getMallAddrByStatus(String userId);

    void editMallAddr(MallAddress mallAddress);

    void deleteMallAddr(String addressId);

    void changeDefaultAddr(String userId);



}
