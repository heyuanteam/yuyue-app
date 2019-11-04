package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.MallComment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallCommentMapper extends MyBaseMapper<MallComment> {

    @Select("SELECT * FROM yuyue_mall_comment WHERE shop_id = #{shopId}")
    List<MallComment> getMallComments(@Param(value = "shopId") String shopId);


    @Select("SELECT * FROM yuyue_mall_comment WHERE shop_id = #{shopId} and consumer_id = #{consumerId} ")
    MallComment getMallComment(@Param(value = "shopId") String shopId,@Param(value = "consumerId")String consumerId);

    @Select("SELECT AVG(score) FROM yuyue_mall_comment WHERE shop_id = #{shopId}")
    Double getScore(@Param(value = "shopId") String shopId);

    @Transactional
    @Insert("INSERT INTO yuyue_mall_comment (comment_id,consumer_id,shop_id,content,score) " +
            "VALUES (#{commentId},#{consumerId},#{shopId},#{content},#{score})")
    void addMallComment(MallComment mallComment);
}
