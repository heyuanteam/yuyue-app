package com.yuyue.app.utils;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.app.api.domain.ReturnResult;

public class ResultJSONUtils {
    public static JSONObject getJSONObjectBean(ReturnResult returnResult){
        return JSONObject.parseObject(JSONObject.toJSON(returnResult).toString());
    }
}
