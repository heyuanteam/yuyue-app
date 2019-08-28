package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UploadFile;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UploadFileMapper extends MyBaseMapper<UploadFile> {
    @Select("select * from ${tableName} where id = #{id}")
    UploadFile selectById(@Param("tableName")String tableName,@Param("id")String id);

    @Select("select b.id,DATE_FORMAT(b.uploadTime ,'%Y-%m-%d %H:%i:%s') uploadTime,b.filesName,b.filesPath,b.filesType,b.fileSize,b.authorId,b.description,b.commentAmount,b.likeAmount,b.attentionAmount,b.duration " +
            "from ${tableName} b WHERE   b.filesType = 'video' GROUP BY b.id ORDER BY b.uploadTime OR b.commentAmount OR b.likeAmount or b.attentionAmount DESC LIMIT #{bdgin},#{size}")
    List<UploadFile> getVideo(@Param("tableName")String tableName, @Param("bdgin")int bdgin, @Param("size")int size);

    @Transactional
    @Delete("delete from ${tableName} where id = #{id}")
    void deleteById(@Param("tableName")String tableName,@Param("id")String id);

    @Transactional
    @Insert("INSERT INTO ${tableName} " +
            "(id,filesName,filesPath,filesType,authorId,description,vedioAddress) VALUES " +
            "(#{id},#{filesName},#{filesPath},#{filesType},#{authorId},#{description},#{vedioAddress})")
    void insertUploadFile(@Param("tableName") String tableName,@Param("id") String id,@Param("filesName") String filesName,
                          @Param("filesPath") String filesPath, @Param("filesType") String filesType, @Param("authorId") String authorId,
                          @Param("description") String description,@Param("vedioAddress")String vedioAddress);
    //视频表 通过视频id  点赞量+1
    @Transactional
    @Update("UPDATE ${tableName} SET likeAmount = likeAmount  +  1  WHERE id = #{id}")
    void likeAmount(@Param("tableName")String tableName,@Param("id")String id);
    //用户表  通过用户id  点赞量+1
    @Transactional
    @Update("UPDATE yuyue_merchant  SET LIKE_TOTAL =LIKE_TOTAL +1  WHERE id = #{authorId}")
    void userLikeAmount(String authorId);

    //视频表 通过视频id  评论量+1
    @Transactional
    @Update("UPDATE ${tableName} SET commentAmount = commentAmount + 1 WHERE id = #{id}")
    void commentAmount(@Param("tableName")String tableName,@Param("id")String id);

    //用户表  通过用户id  评论量+1
    @Transactional
    @Update("UPDATE yuyue_merchant  SET COMMENT_TOTAL =COMMENT_TOTAL +1  WHERE id = #{authorId} ")
    void userCommentAmount(String authorId);

    //用户表 通过id 关注量+1
    @Transactional
    @Update("UPDATE yuyue_merchant  SET ATTENTION_TOTAL =ATTENTION_TOTAL +1  WHERE id =  #{authorId}")
    void userAttentionAmount(String authorId);


    //删除评论  视频表 通过视频id  评论量-1
    @Transactional
    @Update("UPDATE ${tableName} SET commentAmount = commentAmount - 1 WHERE id = #{id}")
    void delCommentAmount(@Param("tableName")String tableName,@Param("id")String id);

    //删除评论  用户表  通过用户id  评论量-1
    @Transactional
    @Update("UPDATE yuyue_merchant  SET COMMENT_TOTAL =COMMENT_TOTAL -1  WHERE id = #{authorId} ")
    void delUserCommentAmount(String authorId);


    //取消关注 用户表  通过用户id  关注量-1
    @Transactional
    @Update("UPDATE yuyue_merchant  SET ATTENTION_TOTAL =ATTENTION_TOTAL -1  WHERE id =  #{authorId}")
    void reduceAttentionAmount(String authorId);


    @Transactional
    @Update("UPDATE ${tableName} SET categoryId=#{categoryId},title=#{title},description=#{description}  WHERE id = #{id}")
    void getRelease(@Param("tableName")String tableName,@Param("id")String id,@Param("categoryId")String categoryId,
                    @Param("title")String title,@Param("description")String description);
   /* @Transactional
    @Update("UPDATE ${tableName} SET attentionAmount = attentionAmount + 1 WHERE id = #{id}")
    void attentionAmount(@Param("tableName")String tableName,@Param("id")String id);*/








    @Select("SELECT * FROM ${tableName} WHERE authorId = #{authorId} ORDER BY uploadTime DESC ")
    List<UploadFile> getVideoByAuthorId(@Param("tableName") String tableName,@Param("authorId") String authorId);
}
