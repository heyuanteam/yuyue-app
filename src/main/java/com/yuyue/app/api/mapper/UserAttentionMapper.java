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
    @Select("SELECT *,DATE_FORMAT(CREATE_TIME,'%Y-%m-%d %H:%i:%s') createTime" +
            " FROM yuyue_attention WHERE userId = #{userId} AND status = '1' limit #{begin},#{limit}")
    List<Attention> getUserAttention(@Param("userId")String userId,@Param("begin")int begin,@Param("limit")int limit);

    /**
     * 添加关注
     * @param ,userId,authorId
     * @return
     */
    @Transactional
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
     * 取消关注   -->用户删除关注表数据
     * @param userId  authorId
     * @return
     */
    @Transactional
    @Delete("DELETE FROM yuyue_attention WHERE userId = #{userId} and authorId = #{authorId}")
    void cancelAttention(@Param("userId")String userId,@Param("authorId") String authorId);

    /**
     * 取消关注  --> 作者的关注量-1
     * @param authorId
     */
    @Transactional
    @Update("UPDATE yuyue_merchant  SET ATTENTION_TOTAL =ATTENTION_TOTAL -1  WHERE id =  #{authorId}")
    void reduceAttentionAmount(String authorId);

    @Select("SELECT COUNT(userId) from yuyue_attention where authorId = #{ authorId }")
    int getFansSum(String authorId);

    @Select("SELECT STATUS FROM yuyue_attention WHERE userId = #{userId}  AND  authorId = #{authorId}")
    String getAttentionStatus(@Param("userId") String userId,@Param("authorId") String authorId);
}
