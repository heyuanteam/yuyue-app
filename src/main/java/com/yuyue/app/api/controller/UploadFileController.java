package com.yuyue.app.api.controller;
import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.service.UploadFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: Lucifer
 * @create: 2018-12-04 15:36
 * @description:
 **/

@Controller
@RequestMapping(value="/uploadFile", produces = "application/json; charset=UTF-8")
public class UploadFileController {
    private static final Logger log = LoggerFactory.getLogger(UploadFileController.class);

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * 删除单个文件
     *
     * @param id
     */
    @RequestMapping(value = "/delfile")
    @ResponseBody
    public JSONObject delfile(@RequestParam("id")String id) throws Exception {
        return uploadFileService.deleteFile(id);
    }

    /**
     * 批量上传文件到fastdfs服务器
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/uploadServer")
    @ResponseBody
    @LoginRequired
    public JSONObject uploadFileServer(@RequestParam("file") MultipartFile[] files, @CurrentUser AppUser user, String fileType, String vedioAddress) throws Exception {
        return uploadFileService.UploadFilesToServer(files,user,fileType,vedioAddress);
    }

    /**
     * 我的发布
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getRelease")
    @ResponseBody
//    @LoginRequired   @CurrentUser
    public JSONObject getRelease(String id, String categoryId,String title,String description) throws Exception {
        return uploadFileService.getRelease(id,categoryId,title,description);
    }

    /**
     * 单个文件下载
     *
     * @param filesName
     * @param filesPath
     */
    @RequestMapping(value = "downloadFile")
    public void downloadFile(@RequestParam("filesName") String filesName,@RequestParam("filesPath") String filesPath, HttpServletResponse response) throws IOException {
        uploadFileService.downloadFile(filesName, filesPath, response);
    }

    /**
     * 视频点赞
     * @param videoId
     */
    @RequestMapping("likeCount")
    @ResponseBody
    public JSONObject likeCount(String videoId) {
        return uploadFileService.likeCount(videoId);
    }

    /**
     * 视频播放量
     * @param videoId
     */
    @RequestMapping("getVdieoCount")
    @ResponseBody
    public void getVdieoCount(String videoId) {
        uploadFileService.getVdieoCount(videoId);
    }

}
