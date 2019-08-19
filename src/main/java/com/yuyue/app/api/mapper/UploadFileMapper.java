package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.UploadFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.MySqlMapper;

@Repository
public interface UploadFileMapper extends BaseMapper<UploadFile>, MySqlMapper<UploadFile> {
    @Select("select * from yuyue_upload_file where id = #{id}")
    UploadFile selectById(String id);

    @Select("select count(*) from yuyue_upload_file where filesMD5 = #{filesMD5}")
    int selectByFilesMD5(String filesMD5);

    @Transactional
    @Delete("delete from yuyue_upload_file where id = #{id}")
    void deleteById(String id);

    @Transactional
    @Update("UPDATE yuyue_upload_file SET uploadTime = #{uploadTime},filesPath = #{filesPath} WHERE id = #{id}")
    void updateById(UploadFile uploadFile);

    @Transactional
    @Insert("INSERT INTO yuyue_upload_file " +
            "(id,filesName,filesPath,filesType,filesMD5,fileSize,authorId,description,duration) VALUES " +
            "(#{id},#{filesName},#{filesPath},#{filesType},#{filesMD5},#{fileSize},#{authorId},#{description},#{duration}) ")
    void insertUploadFile(UploadFile uploadFile);
}
