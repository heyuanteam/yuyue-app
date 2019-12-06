package com.yuyue.app.api.service;
import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UploadFileService {
    /**
     * 视频详情
     *
     * @return
     */
    UploadFile fileDetail(String userId,String videoId);

    /**
     * 根据文件名称删除文件
     *
     * @return
     */
    JSONObject deleteFile(String authorId,String id) throws Exception;

    /**
     * 上传文件到fastdfs图片服务器
     *
     * @return
     */
    JSONObject UploadFilesToServer(MultipartFile[] files,String version)throws Exception;

    /**
     * 从fastdfs服务器下载文件
     *
     * @param filesName
     * @param filesPath
     */
    void downloadFile(String filesName, String filesPath, HttpServletResponse response) throws IOException;

    /**
     * 视频播放量
     * @param
     */
    void playAmount(String authorId,String videoId);
    /**
     * 视频点赞量
     * @param
     */
    JSONObject likeAcount(String authorId,String videoId);

    /**
     * 视频评论量
     * @param
     */
    //JSONObject commentAmount(String authorId,String videoId);


    /**
     * 视频表,用户表 评论量 +1
     * @param
     */
     JSONObject allRoleCommentAmount(String authorId,String videoId);



    /**
     * 删除评论， 视频表,用户表 评论量 -1
     * @param authorId
     * @param videoId
     */
    void reduceCommentAmount(String authorId,String videoId);
    /**
     * 视频页 获取视频列表 ； 视频种类 ； 视频搜索  并分页
     * @param
     * @param
     * @return
     */
    List<UploadFile> getVideo(String categoryId,String content);

    /**
     * 获取首页视频
     * @param
     *
     * @return
     */
    List<UploadFile> getVideoToHomePage();

    /**
     * 获取下一个视频
     * @param parameter
     * @return
     */
    List<UploadFile> getNextVideo(String parameter,String uploadTime,String type);
    /**
     * 我的发布
     * @param categoryId
     * @param title
     * @param description
     * @return
     */
    JSONObject addRelease(String authorId,String categoryId, String title, String description, String fileType, String videoAddress,String originalImage,String fileName,String filesPath);

    /**
     *(我的发布列表)通过作者id 获取视频
     * @param
     * @return
     */
     List<UploadFile> getVideoByAuthorId(String authorId,int begin,int limit);
    /**
     *用户通过作者id查询本人上传的视频,只展示通过审核的视频
     * @param authorId
     * @return
     */
    List<UploadFile> getVideoByAuthor(String authorId,int begin,int limit);


}
