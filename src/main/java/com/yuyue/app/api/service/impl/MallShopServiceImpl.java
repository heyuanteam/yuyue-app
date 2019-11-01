package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Cart;
import com.yuyue.app.api.domain.MallShop;
import com.yuyue.app.api.domain.ShopImage;
import com.yuyue.app.api.domain.Specification;
import com.yuyue.app.api.mapper.CartMapper;
import com.yuyue.app.api.mapper.MallShopMapper;
import com.yuyue.app.api.mapper.ShopImageMapper;
import com.yuyue.app.api.mapper.SpecificationMapper;
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
}
