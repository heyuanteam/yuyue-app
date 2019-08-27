package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Address;
import com.yuyue.app.api.domain.Banner;
import com.yuyue.app.api.domain.VideoCategory;


import java.util.List;


public interface HomePageService {
    List<Banner> getBanner();
    List<VideoCategory> getVideoCategory();

    List<Address> getAddress();
}
