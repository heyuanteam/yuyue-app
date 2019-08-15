package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.VideoCategory;
import com.yuyue.app.api.mapper.BannerMapper;
import com.yuyue.app.api.mapper.VideoCategoryMapper;
import com.yuyue.app.api.mapper.VideoMapper;
import com.yuyue.app.api.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HomePageServiceImpl implements HomePageService {
    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private VideoCategoryMapper videoCategoryMapper;
    @Override
    public List<Banner> getBanner() {
        List<Banner> banners = bannerMapper.getBanner();
        for (Banner b: banners
             ) {
            System.out.println(b);
        }
        return banners;
    }

    @Override
    public List<VideoCategory> getVideoCategory() {
        List<VideoCategory> videoCategories=videoCategoryMapper.getVideoCategory();
        for (VideoCategory videoCategory:videoCategories
             ) {
            System.out.println(videoCategory);
        }
        return videoCategories;
    }


}
