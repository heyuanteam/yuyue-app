package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Attention;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.mapper.UserAttentionMapper;
import com.yuyue.app.api.service.UserAttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserAttentionServiceImpl implements UserAttentionService {
    @Autowired
    private UserAttentionMapper userAttentionMapper;





    @Override
    public List<Attention> getUserAttention(String userId) {


        return userAttentionMapper.getUserAttention(userId);
    }

    @Override
    public List<Attention> addAttention(String authorId) {
        return null;
    }

    @Override
    public List<Attention> deteleAttention(String authorId) {
        return null;
    }
}
