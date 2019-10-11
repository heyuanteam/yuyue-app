package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuyue.app.annotation.CurrentUser;
import com.yuyue.app.annotation.LoginRequired;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.UploadFile;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.api.service.UserCommentService;
import com.yuyue.app.utils.HttpUtils;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: Lucifer
 * @create: 2018-12-04 15:36
 * @description:
 **/

@Controller
@RequestMapping(value="/uploadFile", produces = "application/json; charset=UTF-8")
public class UploadFileController extends  BaseController{
    private static final Logger log = LoggerFactory.getLogger(UploadFileController.class);

    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private LoginService loginService;

    /**
     * 视频详情
     * @param
     * @return
     */
    @RequestMapping(value = "/fileDetail")
    @ResponseBody
    public JSONObject  fileDetail(String authorId,String videoId, HttpServletRequest request, HttpServletResponse response){
        log.info("视频详情-------------->>/uploadFile/fileDetail");
        getParameterMap(request, response);
        String token = request.getHeader("token");
        String userId = "";
        if(StringUtils.isNotEmpty(token)){
            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
        }
        ReturnResult returnResult=new ReturnResult();
        if(authorId.isEmpty() || videoId.isEmpty()){
            returnResult.setMessage("作者id或视频id不能为空!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        UploadFile uploadFile = uploadFileService.fileDetail(authorId, videoId);
        if(StringUtils.isNull(uploadFile)){
            returnResult.setMessage("视频已删除!!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        Map<String,Object> map= Maps.newTreeMap();
        if (userId.isEmpty()){
            //用户没有登录情况下，显示视频点赞量，作者关注量
            map.put("LikeStatus",0);
            map.put("AttentionStatus","0");
        }else {
            String likeStatus=userCommentService.getLikeStatus(userId, videoId);
            //用户未点赞该视频
            if(StringUtils.isEmpty(likeStatus) || "0".equals(likeStatus)){
                map.put("LikeStatus","0");
            }else
            //已点赞
            map.put("LikeStatus","1");
            String attentionStatus = userCommentService.getAttentionStatus(userId, authorId);
//            System.out.println(likeStatus+"-----"+attentionStatus);
            //用户关注该视频作者状态
            if (StringUtils.isEmpty(attentionStatus) || "0".equals(attentionStatus))
            map.put("AttentionStatus","0");
            else
            map.put("AttentionStatus","1");
        }
        AppUser appUserMsg = loginService.getAppUserMsg("", "", uploadFile.getAuthorId());
        map.put("AuthorName",appUserMsg.getNickName());
        map.put("HeadUrl",appUserMsg.getHeadpUrl());
        map.put("fileDetail",uploadFile);
//        System.out.println(map);
        returnResult.setMessage("返回成功!!");
        returnResult.setStatus(Boolean.TRUE);
        log.info("====videoId===="+videoId);
        log.info("====authorId===="+authorId);
        returnResult.setResult(uploadFileService.fileDetail(authorId,videoId));
        returnResult.setResult(map);
        uploadFileService.playAmount(authorId,videoId);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 删除单个文件
     *
     * @param id
     */
    @RequestMapping(value = "/delfile")
    @ResponseBody
    public JSONObject delfile(@RequestParam("id")String id,String authorId, HttpServletRequest request, HttpServletResponse response)throws Exception {
        log.info("删除单个文件-------------->>/uploadFile/delfile");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        if(authorId.isEmpty() || id.isEmpty()){
            returnResult.setMessage("作者id或视频id不能为空!!");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        return uploadFileService.deleteFile(authorId,id);
    }

    /**
     * 批量上传文件到fastdfs服务器
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/uploadServer")
    @ResponseBody
    public JSONObject uploadFileServer(@RequestParam("file") MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("批量上传文件到fastdfs服务器-------------->>/uploadFile/uploadServer");
        getParameterMap(request, response);
//        Map<String, String> mapValue = getParameterMap(request);
//        String token = request.getHeader("token");
//        String userId = "";
//        if(StringUtils.isNotEmpty(token)){
//            userId = String.valueOf(JWT.decode(token).getAudience().get(0));
//        }
        return uploadFileService.UploadFilesToServer(files);
    }

    /**
     * 视频发布
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addRelease")
    @ResponseBody
    @LoginRequired
    public JSONObject addRelease(@CurrentUser AppUser user,String categoryId,String title,String description, String fileType,
                                 String videoAddress,String fileName,String filesPath, HttpServletRequest request
                                    , HttpServletResponse response) throws Exception {
        log.info("视频发布-------------->>/uploadFile/addRelease");
        getParameterMap(request, response);
        return uploadFileService.addRelease(user.getId(),categoryId,title,description,fileType,videoAddress,fileName,filesPath);
    }

    /**
     * 单个文件下载
     *
     * @param filesName
     * @param filesPath
     */
    @RequestMapping(value = "/downloadFile")
    public void downloadFile(@RequestParam("filesName") String filesName,@RequestParam("filesPath") String filesPath,
                             HttpServletResponse response, HttpServletRequest request) throws IOException {
        log.info("单个文件下载-------------->>/uploadFile/downloadFile");
        getParameterMap(request, response);
        uploadFileService.downloadFile(filesName, filesPath, response);
    }

    /**
     * 获取视频列表
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping("/getVideo")
    public JSONObject getVideo(String page,String categoryId,String content,HttpServletRequest request, HttpServletResponse response){
        log.info("获取视频列表-----视频分类-----视频搜索---->>/uploadFile/getVideo");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        List<UploadFile> list = Lists.newArrayList();
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))  page = "1";
        PageHelper.startPage(Integer.parseInt(page), 10);
        List<UploadFile> uploadFileList = uploadFileService.getVideo(categoryId,content);

        System.out.println(uploadFileList.size());
        PageInfo<UploadFile> pageInfo=new PageInfo<>(uploadFileList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
//        System.out.println("total:" + total + "  pages: "+pages+"  currentPage:"+currentPage);
        returnResult.setResult(uploadFileList);
        if (pages < currentPage){
            returnResult.setMessage("暂无视频！");
            returnResult.setResult(list);
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        if(CollectionUtils.isEmpty(list)){
            returnResult.setMessage("暂无视频！");
        } else {
            returnResult.setMessage("视频请求成功！");
        }
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}
