package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Attention;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserAttentionMapper extends MyBaseMapper<Attention> {

    /**
     * 查询用户所有的关注
     * @param userId
     * @return
     */
    @Select("SELECT authorId FROM yuyue_attention WHERE userId = #{userId} AND status = '1'")
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
