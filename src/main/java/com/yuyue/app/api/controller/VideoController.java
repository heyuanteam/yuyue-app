package com.yuyue.app.api.controller;


import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.Video;
import com.yuyue.app.api.service.VideoService;
import com.yuyue.app.utils.ResultJSONUtils;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping(value = "jsp" , produces = "application/json; charset=UTF-8")
public class VideoController extends  BaseController{
    private Logger LOGGER= (Logger) LoggerFactory.getLogger(VideoController.class);

    @Value("${video.localPath}")
    private String localPath;

    @Autowired
    private VideoService videoService;

    @RequestMapping("upVideo")
    @ResponseBody
    public JSONObject upVideo(HttpServletRequest request,MultipartFile file){
        ReturnResult returnResult=new ReturnResult();
        LOGGER.info("upvideo is starting");

        Map<String,String> map = getParameterMap(request);
        if(file.isEmpty()){
            returnResult.setMessage("文件不能为空!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
          }else if(file.getSize() > 104857600){
            System.out.println("--------->");
            returnResult.setMessage("上传文件不可大于100MB!");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }else {
            //获取文件名
            String fileName=file.getOriginalFilename();
            String newName=new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())+fileName;
            System.out.println("修改后的名字"+newName);
            String realPath=localPath+File.separator+newName;
            LOGGER.info("文件绝对路径--------------->"+realPath);
            File localFile=new File(realPath);

            //判断文件父目录是否存在
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdir();
            }
            try {
                file.transferTo(localFile);
                Video video=new Video();
                video.setId(UUID.randomUUID().toString().replace("-","").toUpperCase());
                LOGGER.info("----->"+video.getId());
                video.setTitle(file.getOriginalFilename());
                double fileSize=(double)file.getSize();
                System.out.println("获取文件大小======="+fileSize);
                //数据转化   B-->KB-->MB
                DecimalFormat df = new DecimalFormat("#.00");
                if(fileSize < 1024){
                    video.setSize(fileSize+"B");
                }else if (fileSize > 1024 && fileSize < 1048576){
                    System.out.println(fileSize / 1024);
                    video.setSize(df.format(fileSize/1024)+"KB");
                }else {
                    System.out.println(fileSize/1024/1024);
                    video.setSize(df.format(fileSize/1024/1024)+"MB");
                }
                System.out.println("转换后获取文件大小====="+video.getSize());

                video.setAuthorId("1EAAFB67C2094E24A3100C719335FE45");
                video.setUrl(realPath);
                video.setCategory("DANCE");
                video.setDescription("GOOD");
                video.setLikeAmount(0);
                video.setPlayAmount(0);
                System.out.println(video);

                //获取文件时长
                Encoder encoder=new Encoder();
                try {
                    long duration = encoder.getInfo(localFile).getDuration();
                    System.out.println("视频时长"+duration);
                    //时分秒
                    long secondTotal=duration/1000;
                    if (secondTotal < 60){
                        video.setDuration("0:"+secondTotal);
                    }else if(secondTotal > 60 && secondTotal < 3600){
                        int minute = (int)secondTotal / 60;
                        int second=(int)secondTotal  %  60;
                        video.setDuration(minute+":"+second);
                    }else {
                        int hour=(int)secondTotal / 3600;
                        int minute = (int)secondTotal / 60;
                        int second=(int)secondTotal  %  60;
                        video.setDuration(hour+":"+minute+":"+second);
                    }
                } catch (EncoderException e) {
                    e.printStackTrace();
                    returnResult.setMessage("上传视频失败！");
                    LOGGER.info("上传视频失败！");
                }
                System.out.println("时间格式转换后"+video.getDuration());
                videoService.addVideo(video);
                System.out.println("---------------------------------------------------------------------------------->");
            } catch (IOException e) {
                e.printStackTrace();
                returnResult.setMessage("上传视频失败！");
                LOGGER.info("上传视频失败！");
            }
            returnResult.setMessage("上传成功！");
            returnResult.setStatus(Boolean.TRUE);
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}
