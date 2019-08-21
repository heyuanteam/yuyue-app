package com.yuyue.app.api.service;
import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.UploadFileVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UploadFileService {

    /**
     * 根据文件名称删除文件
     *
     * @return
     */
    JSONObject deleteFile(String id) throws Exception;

    /**
     * 上传文件到fastdfs图片服务器
     *
     * @return
     */
    JSONObject UploadFilesToServer(MultipartFile[] files, AppUser user,String fileType,String vedioAddress)throws Exception;

    /**
     * 从fastdfs服务器下载文件
     *
     * @param filesName
     * @param filesPath
     */
    void downloadFile(String filesName, String filesPath, HttpServletResponse response) throws IOException;

    /**
     * 视频点赞
     * @param id
     */
    JSONObject likeCount(String id);

    /**
     * 默认每张表查询五表记录
     * @param bdgin
     * @param size
     * @return
     */
    List<UploadFileVo> getVdeio(String tableName, int bdgin, int size);

    /**
     * 视频播放量
     * @param id
     */
    void getVdieoCount(String id);

    /**
     * 我的发布
     * @param id
     * @param categoryId
     * @param title
     * @param description
     * @return
     */
    JSONObject getRelease(String id, String categoryId, String title, String description);
}
