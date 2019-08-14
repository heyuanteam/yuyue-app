package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Video;
import com.yuyue.app.api.mapper.VideoMapper;
import com.yuyue.app.api.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Override
    public void addVideo(Video video) {
        videoMapper.addVideo(video);
    }
}
