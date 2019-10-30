package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.MallShop;
import com.yuyue.app.api.domain.ShopImage;
import com.yuyue.app.api.domain.Specification;
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

    @Override
    public MallShop getMyMallShop(String shopId) {
        return mallShopMapper.getMyMallShop(shopId);
    }

    @Override
    public List<MallShop> getAllMallShop(String myArea) {
        return mallShopMapper.getAllMallShop(myArea);
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
}
