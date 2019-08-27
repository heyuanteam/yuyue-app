package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Address;
import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.VideoCategory;
import com.yuyue.app.api.mapper.AddressMapper;
import com.yuyue.app.api.mapper.BannerMapper;
import com.yuyue.app.api.mapper.VideoCategoryMapper;
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


}
