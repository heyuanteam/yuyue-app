package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.ReportVideo;
import com.yuyue.app.api.domain.UploadFile;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UploadFileMapper extends MyBaseMapper<UploadFile> {
    /**
     * 获取视频详情
     * @param tableName
     * @param id
     * @return
     */
    @Select("select *,DATE_FORMAT(UPLOAD_TIME,'%Y-%m-%d %H:%i:%s') uploadTime from ${tableName} where id = #{id} and status = '10B'")
    UploadFile selectById(@Param("tableName")String tableName,@Param("id")String id);

    /**
     * 通过视频种类  获取视频详情列表
     * @param
     * @param
     * @return
     */
    List<UploadFile> getVideo(@Param("categoryId")String categoryId,@Param("content")String content);

    /**
     * 通过视频种类  获取视频详情列表
     * @param
     * @param
     * @return
     */
    List<UploadFile> getVideoToHomePage();


    List<UploadFile> getNextVideo(@Param(value = "parameter") String parameter,
                                  @Param(value = "uploadTime")String uploadTime,
                                  @Param(value = "type") String type);


    /**
     * 删除视频
     * @param tableName
     * @param id
     */
    @Transactional
    @Delete("delete from ${tableName} where id = #{id}")
    void deleteById(@Param("tableName")String tableName,@Param("id")String id);


    /**
     * 添加视频信息（发布）
     * @param tableName
     * @param id
     * @param categoryId
     * @param title
     * @param description
     */
    @Transactional
    @Insert("INSERT INTO ${tableName} " +
            "(id,filesName,filesPath,filesType,authorId,description,videoAddress,originalImage,title,categoryId) VALUES " +
            "(#{id},#{filesName},#{filesPath},#{filesType},#{authorId},#{description},#{videoAddress},#{originalImage}," +
            "#{title},#{categoryId})")
    void insertUploadFile(@Param("tableName") String tableName,@Param("id") String id,@Param("filesName") String filesName,
                          @Param("filesPath") String filesPath, @Param("filesType") String filesType, @Param("authorId") String authorId,
                          @Param("description") String description,@Param("videoAddress")String videoAddress,
                          @Param("originalImage")String originalImage,
                          @Param("title")String title,@Param("categoryId")String categoryId);


    /**
     * 更新视频信息（发布）
     * @param tableName
     * @param id
     * @param categoryId
     * @param title
     * @param description
     */
    @Transactional
    @Update("UPDATE ${tableName} SET categoryId=#{categoryId},title=#{title},description=#{description}" +
            "WHERE id = #{id}")
    void addRelease(@Param("tableName")String tableName,@Param("id")String id,@Param("categoryId")String categoryId,
                    @Param("title")String title,@Param("description")String description);


    /**
     * 视频表 通过视频id  点赞量+1
     * @param tableName
     * @param id
     */
    @Transactional
    @Update("UPDATE ${tableName} SET likeAmount = likeAmount  +  1  WHERE id = #{id}")
    void likeAmount(@Param("tableName")String tableName,@Param("id")String id);


    /**
     * 视频表 通过视频id  播放量+1
     * @param tableName
     * @param id
     */
    @Transactional
    @Update("UPDATE ${tableName} SET playAmount = playAmount  +  1  WHERE id = #{id}")
    void playAmount(@Param("tableName")String tableName,@Param("id")String id);


    /**
     * 用户表  通过用户id  点赞量+1
     * @param authorId
     */
    @Transactional
    @Update("UPDATE yuyue_merchant  SET LIKE_TOTAL =LIKE_TOTAL +1  WHERE id = #{authorId}")
    void userLikeAmount(String authorId);


    /**
     * 视频表 通过视频id  评论量+1
     * @param tableName
     * @param id
     */
    @Transactional
    @Update("UPDATE ${tableName} SET commentAmount = commentAmount + 1 WHERE id = #{id}")
    void commentAmount(@Param("tableName")String tableName,@Param("id")String id);


    /**
     * 用户表  通过艺人id  评论量+1
     * @param authorId
     */
    @Transactional
    @Update("UPDATE yuyue_merchant  SET COMMENT_TOTAL =COMMENT_TOTAL + 1  WHERE id = #{authorId} ")
    void userCommentAmount(String authorId);





    /**
     * 删除评论接口中的 评论量 -1（视频表评论量）
     * @param tableName
     * @param id
     */
    @Transactional
    @Update("UPDATE ${tableName} SET commentAmount = commentAmount - 1 WHERE id = #{id}")
    void delCommentAmount(@Param("tableName")String tableName,@Param("id")String id);





    /**
     * 删除评论接口中的 评论量 -1（用户表评论量）
     * @param authorId
     */
    @Transactional
    @Update("UPDATE yuyue_merchant  SET COMMENT_TOTAL =COMMENT_TOTAL -1  WHERE id = #{authorId} ")
    void delUserCommentAmount(String authorId);




   /* @Transactional
    @Update("UPDATE ${tableName} SET attentionAmount = attentionAmount + 1 WHERE id = #{id}")
    void attentionAmount(@Param("tableName")String tableName,@Param("id")String id);*/




    /**
     * 我的发布（作者）
     * @param tableName
     * @param authorId
     * @return
     */
    @Select("SELECT *,DATE_FORMAT(UPLOAD_TIME,'%Y-%m-%d %H:%i:%s') uploadTime FROM ${tableName} WHERE authorId = #{authorId} ORDER BY UPLOAD_TIME DESC limit #{begin},#{limit}")
    List<UploadFile> getVideoByAuthorId(@Param("tableName") String tableName,@Param("authorId") String authorId,@Param("begin")int begin,@Param("limit")int limit);






    /**
     * 用户关注的作者视频,仅展示通过审核的作品
     * @param tableName
     * @param authorId
     * @return
     */
    @Select("SELECT *,DATE_FORMAT(UPLOAD_TIME,'%Y-%m-%d %H:%i:%s') uploadTime FROM ${tableName} WHERE authorId = #{authorId} AND status = '10B' ORDER BY UPLOAD_TIME DESC limit #{begin},#{limit}")
    List<UploadFile> getVideoByAuthor(@Param("tableName") String tableName,@Param("authorId") String authorId,@Param("begin")int begin,@Param("limit")int limit);

    @Transactional
    @Insert("insert into yuyue_video_report (id,user_id,video_id,author_id,content,contact,image_path,status) " +
            "values (#{id},#{userId},#{videoId},#{authorId},#{content},#{contact},#{imagePath},#{status})")
    void reportVideo(ReportVideo reportVideo);

    @Select("select * from yuyue_video_report where user_id = #{userId} and video_id = #{videoId}")
    ReportVideo getReportVideo(@Param(value = "userId") String userId, @Param(value = "videoId") String videoId);

}
