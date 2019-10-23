package com.yuyue.app.utils;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 高德API工具类
 */
public class GouldUtils {

//    连接池最大连接数
    private static final int MAX_TOTAL_CONNECTIONS = 4000;
//    设置每个路由上的默认连接个数
    private static final int DEFAULT_MAX_PER_ROUTE = 200;
//    请求的请求超时时间 单位：毫秒
    private static final int REQUEST_CONNECTION_TIMEOUT = 8 * 1000;
//    请求的等待数据超时时间 单位：毫秒
    private static final int REQUEST_SOCKET_TIMEOUT = 8 * 1000;
//    请求的连接超时时间 单位：毫秒
    private static final int REQUEST_CONNECTION_REQUEST_TIMEOUT = 5 * 1000;
//    连接闲置多久后需要重新检测 单位：毫秒
    private static final int VALIDATE_AFTER_IN_ACTIVITY = 2 * 1000;
//    关闭Socket时，要么发送完所有数据，要么等待多少秒后，就关闭连接，此时socket.close()是阻塞的　单位秒
    private static final int SOCKET_CONFIG_SO_LINGER = 60;
//    接收数据的等待超时时间,即读超时时间，单位ms
    private static final int SOCKET_CONFIG_SO_TIMEOUT = 5 * 1000;
//    重试次数
    private static int RETRY_COUNT = 5;
//    声明为 static volatile,会迫使线程每次读取时作为一个全局变量读取
    private static volatile CloseableHttpClient httpClient = null;

    /**
     * @param uri
     * @return String
     * @description get请求方式
     * @author: long.he01
     */
    public static String doGet(String uri) {
        String responseBody;
        HttpGet httpGet = new HttpGet(uri);
        try {
            httpGet.setConfig(getRequestConfig());
            responseBody = executeRequest(httpGet);
        } catch (IOException e) {
            throw new RuntimeException("httpclient doGet方法异常 ", e);
        } finally {
            httpGet.releaseConnection();
        }
        return responseBody;
    }

    /**
     * @param uri
     * @param params
     * @return string
     * @description 带map参数get请求, 此方法会将map参数拼接到连接地址上。
     */
    public static String doGet(String uri, Map<String, String> params) {
        return doGet(getGetUrlFromParams(uri, params));
    }

    /**
     * @param uri
     * @param params
     * @return String
     * @description 根据map参数拼接完整的url地址
     */
    private static String getGetUrlFromParams(String uri, Map<String, String> params) {
        List<BasicNameValuePair> resultList = FluentIterable.from(params.entrySet()).transform(
                new Function<Map.Entry<String, String>, BasicNameValuePair>() {
                    @Override
                    public BasicNameValuePair apply(Map.Entry<String, String> innerEntry) {
                        return new BasicNameValuePair(innerEntry.getKey(), innerEntry.getValue());
                    }
                }).toList();

        String paramSectionOfUrl = URLEncodedUtils.format(resultList, Consts.UTF_8);
        StringBuffer resultUrl = new StringBuffer(uri);
        if (StringUtils.isEmpty(uri)) {
            return uri;
        } else {
            if (!StringUtils.isEmpty(paramSectionOfUrl)) {
                if (uri.endsWith("?")) {
                    resultUrl.append(paramSectionOfUrl);
                } else {
                    resultUrl.append("?").append(paramSectionOfUrl);
                }
            }
            return resultUrl.toString();
        }
    }

    /**
     * @param uri
     * @param params
     * @return String
     * @description 带map参数的post请求方法
     */
    public static String doPost(String uri, Map<String, String> params) {
        String responseBody;
        HttpPost httpPost = new HttpPost(uri);
        try {
            List<NameValuePair> nvps = Lists.newArrayList();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                nvps.add(new BasicNameValuePair(key, value));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            httpPost.setConfig(getRequestConfig());
            responseBody = executeRequest(httpPost);
        } catch (Exception e) {
            throw new RuntimeException("httpclient doPost方法异常 ", e);
        } finally {
            httpPost.releaseConnection();
        }
        return responseBody;
    }

    /**
     * @param uri
     * @param param
     * @param contentType 根据具体请求情况指定,比如json可以是 ContentType.APPLICATION_JSON
     * @return String
     * @description 带单string参数执行post方法
     */
    public static String doPost(String uri, String param, ContentType contentType) {
        String responseBody;
        HttpPost httpPost = new HttpPost(uri);
        try {
            StringEntity reqEntity = new StringEntity(param, contentType);
            httpPost.setEntity(reqEntity);
            httpPost.setConfig(getRequestConfig());
            responseBody = executeRequest(httpPost);
        } catch (IOException e) {
            throw new RuntimeException("httpclient doPost方法异常 ", e);
        } finally {
            httpPost.releaseConnection();
        }
        return responseBody;
    }

    /**
     * @return RequestConfig
     * @description: 获得请求配置信息
     */
    private static RequestConfig getRequestConfig() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                //.setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                //.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                //.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();
        return RequestConfig.copy(defaultRequestConfig)
                .setSocketTimeout(REQUEST_CONNECTION_TIMEOUT)
                .setConnectTimeout(REQUEST_SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_CONNECTION_REQUEST_TIMEOUT)
                .build();
    }


    /**
     * @param method
     * @return String
     * @throws IOException
     * @description 通用执行请求方法
     */
    private static String executeRequest(HttpUriRequest method) throws IOException {
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                String result;
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                    HttpEntity entity = response.getEntity();
                    result = entity != null ? EntityUtils.toString(entity) : null;
                    EntityUtils.consume(entity);
                    return result;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        String result = getHttpClientInstance().execute(method, responseHandler);
        return result;
    }


    /**
     * @return CloseableHttpClient
     * @description 单例获取httpclient实例
     */
    private static CloseableHttpClient getHttpClientInstance() {
        if (httpClient == null) {
            synchronized (CloseableHttpClient.class) {
                if (httpClient == null) {
                    httpClient = HttpClients.custom().setConnectionManager(initConfig()).setRetryHandler(getRetryHandler()).build();
                }
            }
        }
        return httpClient;
    }

    /**
     * @return HttpRequestRetryHandler
     * @description :获取重试handler
     */
    private static HttpRequestRetryHandler getRetryHandler() {
        // 请求重试处理
        return new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= RETRY_COUNT) {
                    // 假设已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    // 假设server丢掉了连接。那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {
                    // 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // 目标server不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL握手异常
                    return false;
                }
                HttpRequest request = HttpClientContext.adapt(context).getRequest();
                // 假设请求是幂等的，就再次尝试
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };
    }


    /**
     * @return PoolingHttpClientConnectionManager
     * @description 初始化连接池等配置信息
     */
    private static PoolingHttpClientConnectionManager initConfig() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        /**
         * 以下参数设置含义分别为:
         * 1 是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
         * 2 是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
         * 3 接收数据的等待超时时间，单位ms
         * 4 关闭Socket时，要么发送完所有数据，要么等待多少秒后，就关闭连接，此时socket.close()是阻塞的
         * 5 开启监视TCP连接是否有效
         * 其中setTcpNoDelay(true)设置是否启用Nagle算法，设置true后禁用Nagle算法，默认为false（即默认启用Nagle算法）。
         * Nagle算法试图通过减少分片的数量来节省带宽。当应用程序希望降低网络延迟并提高性能时，
         * 它们可以关闭Nagle算法，这样数据将会更早地发 送，但是增加了网络消耗。 单位为：毫秒
         */
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoReuseAddress(true)
                .setSoTimeout(SOCKET_CONFIG_SO_TIMEOUT)
                //.setSoLinger(SOCKET_CONFIG_SO_LINGER)
                //.setSoKeepAlive(true)
                .build();
        connManager.setDefaultSocketConfig(socketConfig);
//        connManager.setValidateAfterInactivity(VALIDATE_AFTER_IN_ACTIVITY);
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        return connManager;
    }

    /**
     * 拼接请求字符串
     *
     * @param params
     * @param output
     * @param key
     * @param url
     * @return
     * @throws IOException
     */
    public static String jointUrl(Map<String, String> params, String output, String key, String url) throws IOException {
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(url);
        int index = 0;
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        for (Map.Entry<String, String> param : entrys) {
            // 判断是否是第一个参数
            if (index == 0) {
                baseUrl.append("?");
            } else {
                baseUrl.append("&");
            }
            baseUrl.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), "utf-8"));
            index++;
        }
        baseUrl.append("&output=").append(output).append("&key=").append(key);
        return baseUrl.toString();
    }

    /*
     * 过滤掉html里不安全的标签，不允许用户输入这些标�?
     */
    public static String htmlFilter(String inputString) {
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

    /**
     * 关键字截取
     * @param keyWord
     * @return
     */
    public static String getKeyWord(String keyWord){
        StringBuffer sb = new StringBuffer();
        if (com.yuyue.app.utils.StringUtils.isNotEmpty(keyWord) && keyWord.contains(" ")) {
            String[] str = keyWord.split(" ");
            for (int i = 0; i < str.length; i++) {
                if (i == 0) {
                    sb.append(str[i]);
                } else {
                    sb.append("+" + str[i]);
                }
            }
        } else {
            sb.append(keyWord);
        }
        if (com.yuyue.app.utils.StringUtils.isEmpty(sb.toString())){
            return "";
        }
        return sb.toString();
    }

    /**
     * 查找附近的门店
     * @param    longitude 经度
     * @param    latitude 纬度
     * @param    distince 距离 (千米)
     * @return   List 符合距离范围的所有的点
     * @Data     2018.10.26
     */
//    public List<PickStoreOfflineModel> findNearbyStore(BigDecimal longitude, BigDecimal latitude, Integer distince) {
//        String[] split = getNearbyByLongitudeAndLatitudeAndDistince(longitude, latitude, distince).split("-");
//        BigDecimal minlng = new BigDecimal(split[0]);
//        BigDecimal maxlng = new BigDecimal(split[1]);
//        BigDecimal minlat = new BigDecimal(split[2]);
//        BigDecimal maxlat = new BigDecimal(split[3]);
//        return pickStoreOfflineDao.findNearbyStore(minlng, maxlng, minlat, maxlat);
//    }

    /**
     *
     * @Description   计算给定经纬度附近相应公里数的经纬度范围
     * @param         longitude 经度
     * @param         latitude 纬度
     * @param         distince 距离（千米）
     * @return        String 格式：经度最小值-经度最大值-纬度最小值-纬度最大值
     * @Data          2018.10.26
     **/
    public static String getNearbyByLongitudeAndLatitudeAndDistince(BigDecimal longitude, BigDecimal latitude, Integer distince) {
        double r = 6371.393;    // 地球半径千米
        double lng = longitude.doubleValue();
        double lat = latitude.doubleValue();
        double dlng = 2 * Math.asin(Math.sin(distince / (2 * r)) / Math.cos(lat * Math.PI / 180));
        dlng = dlng * 180 / Math.PI;// 角度转为弧度
        double dlat = distince / r;
        dlat = dlat * 180 / Math.PI;
        double minlat = lat - dlat;
        double maxlat = lat + dlat;
        double minlng = lng - dlng;
        double maxlng = lng + dlng;
        //sql语句
        return minlng + "-" + maxlng + "-" + minlat + "-" + maxlat;
    }

    /**
     * @Description  计算距离远近并按照距离排序
     * @param        longitude 经度
     * @param        latitude 纬度
     * @param        nearbyStoreList  附近门店
     * @return       按照距离由近到远排序之后List
     */
//    public List<PickStoreOfflineDto> getNearbyStoreByDistinceAsc(BigDecimal longitude, BigDecimal latitude, List<PickStoreOfflineModel> nearbyStoreList) {
//        List<PickStoreOfflineDto> list = new ArrayList<>();
//        nearbyStoreList.forEach(pickStoreOfflineModel -> {
//            PickStoreOfflineDto pickStoreOfflineDto = new PickStoreOfflineDto();
//            BeanUtil.copyProperties(pickStoreOfflineModel, pickStoreOfflineDto);
//            Double distince = getDistince(longitude, latitude,
//                    pickStoreOfflineModel.getLongitude(), pickStoreOfflineModel.getLatitude());
//            pickStoreOfflineDto.setDistince(distince.longValue());
//            list.add(pickStoreOfflineDto);
//        });
//        Collections.sort(list, Comparator.comparing(PickStoreOfflineDto::getDistince));
//        return list;
//    }

    /**
     * @Description     根据经纬度获取两点之间的距离
     * @param           longitude1   地点1经度
     * @param           latitude1    地点1纬度
     * @param           longitude2   地点2经度
     * @param           latitude2    地点2纬度
     * @return          距离：单位 米
     */
    public static Double getDistince(BigDecimal longitude1, BigDecimal latitude1, BigDecimal longitude2, BigDecimal latitude2) {
        double r = 6371.393;         // 地球半径千米
        double lat1 = latitude1.doubleValue();
        double lng1 = longitude1.doubleValue();
        double lat2 = latitude2.doubleValue();
        double lng2 = longitude2.doubleValue();
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * r;
        s = Math.round(s * 1000);
        return s;
    }

    private static Double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
