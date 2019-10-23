package com.yuyue.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(FastdfsUtils.class);

    //微信商户号
    private static final String wxMchID = "1529278811";

    /**
     * get请求
     * 通过url访问并获取返回结果
     * @param requestTokenUrl
     * @return
     */
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

    /**
     * 发送post请求
     *
     * @param url
     *      请求地址
     * @param outputEntity
     *      发送内容
     * @param isLoadCert
     *      是否加载证书
     */
    public static CloseableHttpResponse Post(String url, String outputEntity, boolean isLoadCert) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(new StringEntity(outputEntity, "UTF-8"));
        if (isLoadCert) {
            // 加载含有证书的http请求
            return HttpClients.custom().setSSLSocketFactory(initCert()).build().execute(httpPost);
        } else {
            return HttpClients.custom().build().execute(httpPost);
        }
    }

    /**
     * 加载证书
     */
    public static SSLConnectionSocketFactory initCert() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        FileInputStream inputStream = new FileInputStream(new File("/apiclient_cert.p12"));
//        InputStream inputStream  = this.getClass().getClassLoader().getResourceAsStream("/apiclient_cert.p12");
        InputStream inputStream = new ClassPathResource("apiclient_cert.p12").getInputStream();
        keyStore.load(inputStream, wxMchID.toCharArray());
        if (null != inputStream) {
            inputStream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore,wxMchID.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return sslsf;
    }

    /**
     * 链接和编码
     * @param urlString
     * @param enCode
     * @return
     */
    public static String getReturnData(String urlString,String enCode) {
        String res = "";
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.connect();
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), enCode));
            String line;
            while ((line = in.readLine()) != null) {
                res += line;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 为response设置header，实现跨域
     */
    public static void setHeader(HttpServletRequest request, HttpServletResponse response){
        //跨域的header设置
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
    }

}
