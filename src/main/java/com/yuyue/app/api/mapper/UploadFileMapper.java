package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UploadFile;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface UploadFileMapper extends BaseMapper<UploadFile>, MySqlMapper<UploadFile> {
    @Select("select * from ${tableName} where id = #{id}")
    UploadFile selectById(@Param("tableName")String tableName,@Param("id")String id);

    @Select("select b.id,DATE_FORMAT(b.uploadTime ,'%Y-%m-%d %H:%i:%s') uploadTime,b.filesName,b.filesPath,b.filesType,b.fileSize,b.authorId,b.description,b.playAmount,b.likeAmount,b.duration "
           +"from ${tableName} b WHERE b.filesType = 'vedio' ORDER BY b.uploadTime OR b.playAmount OR b.likeAmount DESC LIMIT #{bdgin},#{size}")
    List<UploadFile> getVdeio(@Param("tableName")String tableName, @Param("bdgin")int bdgin, @Param("size")int size);

    @Transactional
    @Delete("delete from ${tableName} where id = #{id}")
    void deleteById(@Param("tableName")String tableName,@Param("id")String id);

    @Transactional
    @Insert("INSERT INTO ${tableName} " +
            "(id,filesName,filesPath,filesType,filesMD5,fileSize,authorId,description,duration) VALUES " +
            "(#{id},#{filesName},#{filesPath},#{filesType},#{filesMD5},#{fileSize},#{authorId},#{description},#{duration}) ")
    void insertUploadFile(@Param("tableName") String tableName,@Param("id") String id,@Param("filesName") String filesName,
                          @Param("filesPath") String filesPath, @Param("filesType") String filesType,@Param("filesMD5") String filesMD5,
                          @Param("fileSize") String fileSize,@Param("authorId") String authorId, @Param("description") String description,
                          @Param("duration") String duration);
    @Transactional
    @Update("UPDATE ${tableName} SET likeAmount = likeAmount  +  1  WHERE id = #{id}")
    void likeCount(@Param("tableName")String tableName,@Param("id")String id);
}
