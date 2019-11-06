package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.mapper.*;
import com.yuyue.app.api.service.MallShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "mallShopServiceImpl")
public class MallShopServiceImpl implements MallShopService {

    @Autowired
    private MallShopMapper mallShopMapper;
    @Autowired
    private ShopImageMapper shopImageMapper;
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MallCommentMapper commentMapper;
    @Autowired
    private MallOrderItemMapper mallOrderItemMapper;
    @Autowired
    private MallAddressMapper mallAddressMapper;

    @Override
    public MallShop getMyMallShop(String shopId) {
        return mallShopMapper.getMyMallShop(shopId);
    }

    @Override
    public List<MallShop> getAllMallShop(String myArea,String content) {
        return mallShopMapper.getAllMallShop(myArea,content);
    }

    @Override
    public void insertMyMallShop(MallShop mallShop) {
        mallShopMapper.insertMyMallShop(mallShop);
    }

    @Override
    public void updateMyMallShopInfo(MallShop mallShop) {
        mallShopMapper.updateMyMallShopInfo(mallShop);
    }

    @Override
    public ShopImage getShopImage(String shopId) {
        return shopImageMapper.getShopImage(shopId);
    }

    @Override
    public void insertShopImage(ShopImage shopImage) {
        shopImageMapper.insertShopImage(shopImage);
    }

    @Override
    public void deleteShopImage(String imagePath) {
        shopImageMapper.deleteShopImage(imagePath);
    }

    @Override
    public List<Specification> getSpecification(String shopId) {
        return specificationMapper.getSpecification(shopId);
    }

    @Override
    public Specification getSpecificationById(String specificationId) {
        return specificationMapper.getSpecificationById(specificationId);
    }

    @Override
    public void insertSpecification(Specification specification) {
        specificationMapper.insertSpecification(specification);
    }

    @Override
    public void deleteSpecification(String specificationId) {
        specificationMapper.deleteSpecification(specificationId);
    }

    @Override
    public void updateSpecification(Specification specification) {
        specificationMapper.updateSpecification(specification);
    }

    @Override
    public List<Cart> getCarts(String cartId,String  consumerId) {
        return cartMapper.getCarts(cartId,consumerId);
    }

    @Override
    public Cart getCart(String commodityId,String consumerId) {
        return cartMapper.getCart(commodityId,consumerId);
    }

    @Override
    public void editCart(Cart cart) {
        cartMapper.addCart(cart);
    }

    @Override
    public void deleteCart(Cart cart) {
        cartMapper.deleteCart(cart);
    }

    @Override
    public List<MallComment> getMallComments(String shopId) {
        return commentMapper.getMallComments(shopId);
    }

    @Override
    public MallComment getMallComment(String shopId, String consumerId) {
        return commentMapper.getMallComment(shopId,consumerId);
    }

    @Override
    public double getScore(String shopId) {
        return commentMapper.getScore(shopId);
    }

    @Override
    public void addMallComment(MallComment mallComment) {
        commentMapper.addMallComment(mallComment);
    }

    @Override
    public List<OrderItem> getMallOrderItem(String orderId) {
        return mallOrderItemMapper.getMallOrderItem(orderId);
    }

    @Override
    public void editMallOrderItem(OrderItem orderItem) {
        mallOrderItemMapper.editMallOrderItem(orderItem);
    }

    @Override
    public List<MallAddress> getMallAddrByUserId(String userId) {
        return mallAddressMapper.getMallAddrByUserId(userId);
    }

    @Override
    public MallAddress getMallAddress(String addressId) {
        return mallAddressMapper.getMallAddress(addressId);
    }

    @Override
    public void editMallAddr(MallAddress mallAddress) {
        mallAddressMapper.editMallAddr(mallAddress);
    }

    @Override
    public void deleteMallAddr(String addressId) {
        mallAddressMapper.deleteMallAddr(addressId);
    }
}
