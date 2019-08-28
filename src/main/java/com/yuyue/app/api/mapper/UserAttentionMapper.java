package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Attention;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface UserAttentionMapper extends MyBaseMapper<Attention> {

    /**
     * 查询用户所有的关注
     * @param userId
     * @return
     */
    @Select("SELECT * FROM yuyue_attention WHERE userId = #{userId} AND status = '1'")
    List<Attention> getUserAttention(String userId);

    /**
     * 添加关注
     * @param ,userId,authorId
     * @return
     */
    @Insert("INSERT into yuyue_attention (id,userId,authorId) " +
            "VALUES (#{id}, #{userId}, #{authorId})")
    void addAttention(@Param("id") String id, @Param("userId")String userId,@Param("authorId") String authorId);

    /**
     * 作者关注总数+1
     * @param authorId
     */
    @Transactional
    @Update("UPDATE yuyue_merchant  SET ATTENTION_TOTAL =ATTENTION_TOTAL +1  WHERE id =  #{authorId}")
    void userAttentionAmount(String authorId);
    /**
     * 用户删除关注
     * @param userId  authorId
     * @return
     */
    @Delete("DELETE FROM yuyue_attention WHERE userId = #{userId} and authorId = #{authorId}")
    void cancelAttention(@Param("userId")String userId,@Param("authorId") String authorId);

    @Select("SELECT COUNT(userId) from yuyue_attention where authorId = #{ authorId }")
    int getFansSum(String authorId);
}
