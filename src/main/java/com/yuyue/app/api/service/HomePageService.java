package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.*;

import java.util.List;


public interface HomePageService {
    List<Banner> getBanner();
    List<VideoCategory> getVideoCategory();

    List<Address> getAddress();
    YuyueSite getSite(String id);
    void updateSite(String id);
    List<YuyueSite> getSiteList();
    List<SiteShow> getShow(String siteId);
    void addSitePerson(YuyueSitePerson yuyueSitePerson);
    YuyueSitePerson getSitePerson(String userId, String siteId);

}
