package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UploadFile;
import com.yuyue.app.api.domain.UploadFileVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UploadFileMapper extends MyBaseMapper<UploadFile> {
    @Select("select * from ${tableName} where id = #{id}")
    UploadFile selectById(@Param("tableName")String tableName,@Param("id")String id);

    @Select("select b.id,COUNT(c.ID) count,DATE_FORMAT(b.uploadTime ,'%Y-%m-%d %H:%i:%s') uploadTime,b.filesName,b.filesPath,b.filesType,b.fileSize,b.authorId,b.description,b.playAmount,b.likeAmount,b.duration " +
            "from ${tableName} b,yuyue_user_comment c WHERE c.VIDEO_ID=b.id and b.filesType = 'vedio' GROUP BY b.id ORDER BY b.uploadTime OR b.playAmount OR b.likeAmount DESC LIMIT #{bdgin},#{size}")
    List<UploadFileVo> getVdeio(@Param("tableName")String tableName, @Param("bdgin")int bdgin, @Param("size")int size);

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

    @Transactional
    @Update("UPDATE ${tableName} SET likeAmount = likeAmount  +  1  WHERE id = #{id}")
    void likeCount(@Param("tableName")String tableName,@Param("id")String id);

    @Transactional
    @Update("UPDATE ${tableName} SET categoryId=#{categoryId},title=#{title},description=#{description}  WHERE id = #{id}")
    void getRelease(@Param("tableName")String tableName,@Param("id")String id,@Param("categoryId")String categoryId,
                       @Param("title")String title,@Param("description")String description);

    @Transactional
    @Update("UPDATE ${tableName} SET playAmount = playAmount + 1 WHERE id = #{id}")
    void getVdieoCount(@Param("tableName")String tableName,@Param("id")String id);
}
