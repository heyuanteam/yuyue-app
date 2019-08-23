package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.Attention;
import com.yuyue.app.api.domain.ReturnResult;
import com.yuyue.app.api.domain.UploadFile;
import com.yuyue.app.api.service.UploadFileService;
import com.yuyue.app.api.service.UserAttentionService;
import com.yuyue.app.utils.ResultJSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("attention")
public class UserAttentionController extends BaseController {
    @Autowired
    private UserAttentionService userAttentionService;
    @Autowired
    private UploadFileService uploadFileService;
    private ReturnResult returnResult =new ReturnResult();



    /**
     * 查询用户所有的关注
     * @param userId
     * @return
     */
    @RequestMapping("getUserAttention")
    @ResponseBody
    public JSONObject getUserAttention(String userId){

        List<Attention> userAttention = userAttentionService.getUserAttention(userId);
        if(userAttention.isEmpty()){
            returnResult.setMessage("该用户没有关注！！");
            returnResult.setStatus(Boolean.TRUE);
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        for (Attention attention: userAttention
             ) {
            System.out.println("作者id:"+attention.getAuthorId());

            List<UploadFile> videoByAuthorId = uploadFileService.getVideoByAuthorId(attention.getAuthorId());
            for (UploadFile uploadFile:videoByAuthorId
                 ) {
                System.out.println("获取作者上传的视频id:"+uploadFile.getId());
            }

        }
        returnResult.setMessage("返回成功！！");
        returnResult.setStatus(Boolean.TRUE);
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 添加关注
     * @param authorId
     * @return
     */
    @RequestMapping("addAttention")
    @ResponseBody
    public JSONObject addAttention(String authorId){

        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 删除用户关注
     * @param authorId
     * @return
     */
    @RequestMapping("deteleAttention")
    @ResponseBody
    public JSONObject deteleAttention(String authorId){


        return ResultJSONUtils.getJSONObjectBean(returnResult);

    }


}
