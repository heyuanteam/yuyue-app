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
    @Autowired
    private YuyueSitePersonMapper yuyueSitePersonMapper;

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

    /**
     * 扫码修改 入场数目，+1
     * @param id
     */
    @Override
    public void updateSite(String id) {
        yuyueSiteMapper.updateSite(id);
    }

    @Override
    public List<YuyueSite> getSiteList() {
        return yuyueSiteMapper.getSiteList();
    }

    @Override
    public List<SiteShow> getShow(String siteId){
        return yuyueSiteShowMapper.getShow(siteId);
    }

    @Override
    public void addSitePerson(YuyueSitePerson yuyueSitePerson) {
        yuyueSitePersonMapper.addSitePerson(yuyueSitePerson);
    }

    @Override
    public YuyueSitePerson getSitePerson(String userId, String siteId) {
        return yuyueSitePersonMapper.getSitePerson(userId,siteId);
    }


}
