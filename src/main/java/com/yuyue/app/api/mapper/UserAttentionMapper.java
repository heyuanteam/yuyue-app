package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Attention;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
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
    @Select("SELECT * FROM yuyue_attention WHERE userId = #{userId} AND status = '1'")
    public List<Attention> getUserAttention(String userId);

    /**
     * 添加关注
     * @param ,userId,authorId
     * @return
     */
    @Insert("INSERT into yuyue_attention (id,userId,authorId) " +
            "VALUES (#{id}, #{userId}, #{authorId})")
    public void addAttention(@Param("id") String id, @Param("userId")String userId,@Param("authorId") String authorId);

    /**
     * 删除用户关注
     * @param userId  authorId
     * @return
     */
    @Delete("DELETE FROM yuyue_attention WHERE userId = #{userId} and authorId = #{authorId}")
    public void cancelAttention(@Param("userId")String userId,@Param("authorId") String authorId);
}
