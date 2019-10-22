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

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                log.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * GET请求数据
     *
     * @param get_url url地址
     * @param content key=value形式
     * @return 返回结果
     * @throws Exception
     */
    public static String sendGetData(String get_url, String content) throws Exception {
        String result = "";
        URL getUrl = null;
        BufferedReader reader = null;
        String lines = "";
        HttpURLConnection connection = null;
        try {
            if (content != null && !content.equals(""))
                get_url = get_url + "?" + content;
            // get_url = get_url + "?" + URLEncoder.encode(content, "utf-8");
            getUrl = new URL(get_url);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            // 取得输入流，并使用Reader读取
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码
            while ((lines = reader.readLine()) != null) {
                result = result + lines;
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
            connection.disconnect();
        }
    }

    /**
     * @param POST_URL url地址
     * @param content  key=value形式
     * @return 返回结果
     * @throws Exception
     */
    public static String sendPostData(String POST_URL, String content) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        BufferedReader reader = null;
        String line = "";
        String result = "";
        try {
            URL postUrl = new URL(POST_URL);
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();

            out = new DataOutputStream(connection.getOutputStream());
            // content = URLEncoder.encode(content, "utf-8");
            // DataOutputStream.writeBytes将字符串中的16位的unicode字符�?8位的字符形式写道流里�?
            out.writeBytes(content);
            out.flush();
            out.close();
            // 获取结果
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码
            while ((line = reader.readLine()) != null) {
                result = result + line;
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
            connection.disconnect();
        }
    }

    /*
     * 过滤掉html里不安全的标签，不允许用户输入这些标�?
     */
    public static String htmlFilter(String inputString) {
        // return inputString;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        try {
            String regEx_script = "<[\\s]*?(script|style)[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?(script|style)[\\s]*?>";
            String regEx_onevent = "on[^\\s]+=\\s*";
            String regEx_hrefjs = "href=javascript:";
            String regEx_iframe = "<[\\s]*?(iframe|frameset)[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?(iframe|frameset)[\\s]*?>";
            String regEx_link = "<[\\s]*?link[^>]*?/>";

            htmlStr = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_onevent, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_hrefjs, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_iframe, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_link, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");

            textStr = htmlStr;
        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;
    }
}
