package com.yuyue.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.Enumeration;
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

    public static  String doPost(String url,String str){
//		SSLClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
//			httpClient = new SSLClient();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            //设置参数
            StringEntity entityParams = new StringEntity(str, "utf-8");
            httpPost.setEntity(entityParams );
            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"utf-8");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 获取本机Ip
     *
     *  通过 获取系统所有的networkInterface网络接口 然后遍历 每个网络下的InterfaceAddress组。
     *  获得符合 <code>InetAddress instanceof Inet4Address</code> 条件的一个IpV4地址
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String localIp(){
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        ip = Ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            log.warn("获取本机Ip失败:异常信息:"+e.getMessage());
        }
        log.info("ip=========>>>>"+ip);
        return ip;
    }

    //获取客户端，外网IP地址
    public static String getIpAddress(HttpServletRequest request, HttpServletResponse response) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getRemoteAddr ();
            if (ip.equals ("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                ip = inet.getHostAddress ();
            }
        }
        log.info("ip=========>>>>"+ip);
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length () > 15) {
            if (ip.indexOf (",") > 0) {
                ip = ip.substring (0, ip.indexOf (","));
            }
        }
        return ip;
    }

}
