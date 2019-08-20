package com.yuyue.app.api.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.google.common.collect.Maps;
import com.yuyue.app.api.controller.LoginController;
import com.yuyue.app.api.domain.*;
import com.yuyue.app.api.mapper.UploadFileMapper;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.utils.MD5Utils;
import com.yuyue.app.utils.ResultJSONUtils;
import com.yuyue.app.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


/**
 * @author: Lucifer
 * @create: 2018-12-04 15:43
 * @description:
 **/
@Slf4j
@Service
public class UploadFileServiceImpl implements UploadFileService {
    @Autowired
    private UploadFileMapper uploadFileMapper;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private LoginController loginController;

    private ReturnResult returnResult=new ReturnResult();

    /**
     * 根据id删除数据库数据,删掉storage文件
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject deleteFile(String id) {
        UploadFile uploadFile = uploadFileMapper.selectById(ResultJSONUtils.getHashValue("yuyue_upload_file_",id),id);
        if (uploadFile == null) {
            returnResult.setMessage("数据库中不存在！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } else {
            try {
                uploadFileMapper.deleteById(ResultJSONUtils.getHashValue("yuyue_upload_file_",id),id);
                String[] split = uploadFile.getFilesPath().split("/");
                this.storageClient.deleteFile(split[1] +"/"+ split[2] +"/"+ split[3] +"/"+ split[4] +"/"+ split[5]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        returnResult.setMessage("删除文件成功!");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 批量上传文件到服务器
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject UploadFilesToServer(MultipartFile[] files, AppUser user,String fileType,String vedioAddress) throws Exception{
        if (files == null || files.length == 0) {
            returnResult.setMessage("文件为空!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        } /*else if (null == user || Boolean.FALSE == loginController.userAuth(user)){
            returnResult.setMessage("未登录!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }*/
        List<String> listMDs = new ArrayList();
        List<UploadFile> lists = new ArrayList();
        HashMap<String,Object> hashMap = Maps.newHashMap();
        for (int i = 0; i < files.length; i++) {
            if(104857600 < files[i].getSize()){
                returnResult.setMessage("上传文件不可大于100MB!");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }else {
                try {
                    String subFileType = files[i].getContentType().substring(files[i].getContentType().indexOf("/") + 1);
                    //上传
                    StorePath storePath = this.storageClient.uploadFile(files[i].getInputStream(), files[i].getSize(), subFileType, null);
                    UploadFile uploadFile = new UploadFile();
                    String uid = UUID.randomUUID().toString().replaceAll("-", "");
                    uploadFile.setId(uid.toUpperCase());
                    uploadFile.setFilesName(files[i].getOriginalFilename());
                    uploadFile.setFilesPath(Variables.ip_home + "/" + storePath.getFullPath());
//                    uploadFile.setFileSize(ResultJSONUtils.getSize(Double.valueOf(files[i].getSize())));
                    if(StringUtils.isNotEmpty(fileType) && "video".equals(fileType)){
//                        uploadFile.setDuration(ResultJSONUtils.getVideoUrl("http://"+uploadFile.getFilesPath()));
                        uploadFile.setFilesType("video");
                        uploadFile.setVedioAddress(vedioAddress);
                    } else {
                        uploadFile.setFilesType("picture");
                    }
//                    uploadFile.setFilesMD5(MD5Utils.getMd5ByUrl("http://"+uploadFile.getFilesPath()));
                    log.info("文件存储在服务器的路径==============>{}", Variables.ip_home + "/" + storePath.getFullPath());

//                    if (uploadFileMapper.selectByFilesMD5(uploadFile.getFilesMD5()) > 0) {
//                        throw new RuntimeException("第" + (i + 1) + "个文件，数据库已存在");
//                    }
                    lists.add(uploadFile);
                    listMDs.add(uploadFile.getFilesPath());
                 /*伪造异常，测试文件部分上传失败，是否会清空此次上传的所有文件
                  fileList.get(10000000);*/
                    uploadFileMapper.insertUploadFile(ResultJSONUtils.getHashValue("yuyue_upload_file_",uid),
                            uploadFile.getId(),uploadFile.getFilesName(),uploadFile.getFilesPath(),uploadFile.getFilesType(),
                            uploadFile.getAuthorId(),uploadFile.getDescription(), uploadFile.getVedioAddress());

//                  uploadFileMapper.insertList(listMDs);
                    //数据库修改
//                uploadFileMapper.updateById(uploadFile);
//                uploadFileList.add(uploadFileMapper.selectById(uploadFiles.get(i).getId()));
                } catch (FileNotFoundException e) {
                    log.info("文件上传失败，正在清理文件==================>,{}", e.getMessage());
                    e.printStackTrace();
                    for (int j = 0; j < listMDs.size(); j++) {
                        String[] split = listMDs.get(j).split("/");
                        this.storageClient.deleteFile(split[1] +"/"+ split[2] +"/"+ split[3] +"/"+ split[4] +"/"+ split[5]);
                    }
                    log.info("文件存储在服务器的失败=======>{}", e.getMessage());
                }
            }
        }
        returnResult.setMessage("上传文件成功!");
        returnResult.setStatus(Boolean.TRUE);
        hashMap.put("uploadFile", JSONArray.parseArray(JSON.toJSONString(lists)));
        returnResult.setResult(hashMap);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 下载文件
     *
     * @param filesName
     * @param filesPath
     */
    @Override
    public void downloadFile(String filesName, String filesPath, HttpServletResponse response) throws IOException {
        //这里的filesPath需要是这种格式的：M00/00/00/wKg7g1wN2YyAAH1MAADJbaKmScw004.jpg
        String[] split = filesPath.split("/");
        filesPath=split[2]+"/"+ split[3] +"/"+ split[4] +"/"+ split[5];
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        //刚开始用的是DownloadFileWriter，结果得到bytes的字节数不对，导致下载后，文件打不开
        //DownloadFileWriter callback = new DownloadFileWriter(filesName);
        String substring = Variables.groupName.substring(1, Variables.groupName.length());
        byte[] bytes = this.storageClient.downloadFile(substring, filesPath, downloadByteArray);
        System.out.println(bytes.length);
        //支持中文名称，避免乱码
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + new String(filesName.getBytes("UTF-8"), "iso-8859-1"));
        response.setCharacterEncoding("UTF-8");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
    }

    @Override
    public void likeCount(String videoId) {
        uploadFileMapper.likeCount(ResultJSONUtils.getHashValue("yuyue_upload_file_",videoId),videoId);
    }

    @Override
    public List<UploadFileVo> getVdeio(String tableName, int bdgin, int size) {
        return uploadFileMapper.getVdeio(tableName,bdgin,size);
    }

    @Override
    public void getVdieoCount(String id) {
        uploadFileMapper.getVdieoCount(ResultJSONUtils.getHashValue("yuyue_upload_file_",id),id);
    }

    /**
     * 校验文件
     *
     * @param file
     * @throws IOException
     */
//    public void checkFileType(int i, MultipartFile file) throws IOException {
//        String type = file.getContentType();
//        //校验文件类型是否被允许可以上传
//        if (!properties.getAllowTypes().contains(type)) {
//            log.info("第" + (i + 1) + "个文件类型不允许上传========>,{}", type);
//            throw new RuntimeException("第" + (i + 1) + "个文件类型不允许上传");
//        }
//        //校验文件内容是否为图片
//        BufferedImage image = ImageIO.read(file.getInputStream());
//        if (image == null) {
//            log.info("第" + (i + 1) + "个文件内容不符合要求");
//            throw new RuntimeException("上传失败，" + "第" + (i + 1) + "个文件内容不符合要求");
//        }
//    }
}
