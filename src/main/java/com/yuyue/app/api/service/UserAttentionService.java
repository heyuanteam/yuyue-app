package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Attention;

import java.util.List;

public interface UserAttentionService {
    /**
     * 查询用户所有的关注
     * @param userId
     * @return
     */
    public List<Attention> getUserAttention(String userId);

    /**
     * 添加关注
     * @param authorId
     * @return
     */
    public List<Attention> addAttention(String authorId);

    /**
     * 删除用户关注
     * @param authorId
     * @return
     */
    public List<Attention> deteleAttention(String authorId);
}
