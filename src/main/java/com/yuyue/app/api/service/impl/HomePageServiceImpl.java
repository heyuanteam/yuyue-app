package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.mapper.*;
import com.yuyue.app.api.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "HomePageService")
public class HomePageServiceImpl implements HomePageService {
    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private VideoCategoryMapper videoCategoryMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private YuyueSiteMapper yuyueSiteMapper;
    @Autowired
    private YuyueSiteShowMapper yuyueSiteShowMapper;

    @Override
    public List<Banner> getBanner() {
        return bannerMapper.getBanner();
    }

    @Override
    public List<VideoCategory> getVideoCategory() {
        return videoCategoryMapper.getVideoCategory();
    }

    @Override
    public List<Address> getAddress() {return addressMapper.getAddress();}

    @Override
    public YuyueSite getSite(String id) {
        return yuyueSiteMapper.getSite(id);
    }

    @Override
    public List<YuyueSite> getSiteList() {
        return yuyueSiteMapper.getSiteList();
    }

    @Override
    public List<SiteShow> getShow(String siteId){
        return yuyueSiteShowMapper.getShow(siteId);
    }



}
