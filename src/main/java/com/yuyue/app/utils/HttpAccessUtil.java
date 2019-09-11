package com.yuyue.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAccessUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastdfsUtils.class);


    public static JSONObject getReturnResult(String requestTokenUrl){
        JSONObject jsonObject =null;
        try {
            URL urlGet = new URL(requestTokenUrl);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            InputStream is = http.getInputStream();
            /*int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);*/

            int size = 0;
            while (size == 0) {
                size = is.available();
            }
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            is.close();
            String message = new String(jsonBytes, "UTF-8");
            jsonObject = JSONObject.parseObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
