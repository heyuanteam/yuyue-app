package com.yuyue.app.enums;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

import java.net.InetSocketAddress;

/**
 * @author: Lucifer
 * @create: 2018-11-11 04:00
 * @description: 定义常用的变量
 **/
public class Variables {

// -------------------------------------------------------------------------------
    public static final String ip = "101.37.252.177";
    //    public static final String ip = "47.97.125.222";
    public static final String appPort = ":28082";
    public static final String bossPort = ":28088";
    // 编码集，支持 GBK/UTF-8
    public static final String CHARSET = "utf-8";
    public static final String OUTPUT = "JSON";
    public static final String info = "杭州和元网络科技有限公司";
    public static final String subject = "商城支付";

    public static final String AliPayReturnUrl = "http://www.heyuannetwork.com/isLogin/shop/list";
    public static final String wxNotifyUrl = "http://"+ip+appPort+"/yuyue-app/pay/wxpayNotify";
    public static final String AliPayNotifyUrl = "http://"+ip+appPort+"/yuyue-app/pay/alipayNotify";

    //    极光库存通知 : 7 (merchantId,shopid)
    public static final String sendStockJPushUrl = "http://"+ip+bossPort+"/yuyue-boss/send/sendStockJPush";
    //    极光商家卖出商品通知 : 8 (orderId)
    public static final String sendClotheSoldUrl = "http://"+ip+bossPort+"/yuyue-boss/send/sendClotheSold";

// -------------------------------------------------------------------------------
    //安装fastdfs的虚拟机的ip
//    public static final String ip_home = "http://101.37.252.177:8888";
    public static final String ip_home = "http://www.heyuannetwork.com";
    //组名，跟你在fastdfs配置文件中的一致
    public static final String groupName = "/group1";
    //默认文件格式，后缀名,设置上传后在fastdfs存储的格式，你可以改成其它格式图片，fastdfs只支持几种常用格式的，自己百度可以查查，jpg和png都是可以的
    public static final String fileExtName = "jpg";
    //超时时间
    public static final int soTimeout = 550;
    //连接时间
    public static final int connectTimeout = 500;

    public final static int port = 22122;
    public final static int store_port = 23000;
    public static InetSocketAddress address = new InetSocketAddress(ip_home, port);
    public static InetSocketAddress store_address = new InetSocketAddress(ip_home, store_port);

// -------------------------------------------------------------------------------
    //微信APPID
    public static final String wxAppId = "wx82e0374be0e044a4";
    //微信AppSecret
    public static final String APP_SECRET = "c08075181dce2ffe3f036734f168318f";
    //微信商户号
    public static final String wxMchID = "1529278811";
    //微信秘钥
    public static final String wxKEY = "FE79E95059CDCA91646CDDA6A7F60A93";

// -------------------------------------------------------------------------------
    //支付宝
    public static final String AliAPPID = "2019082166401163";
    public static final String AliAppPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDD5vbz+U6bkpwHTnto4fg2" +
            "er1oyxMHnGOPGeEl0MA2xuLtcTw8nPqp+ZMLGRWZxr7YLXv/372G6017ruKUE9Wh3ZrxestCDxDnhi7hfeLFPcsIryhpBTchfd7NhPJngGSUS8S" +
            "O4W+x5Yco40fITdnTrGz+fFCnRuUOU7dzo4tHCkrkJlX4cZ6D1PR9IxRCvlCFbxYzTBsqVpn5Ekc+B3RcxycX+yP7CdFSn7frp+uJBHfwDz//NK" +
            "X7OJbJDflNCacU4AFzF8M6wqs7bwIhi0GCZyZeS7FeCFuSAp7MW7Mk6OFDW/OM5bKftu+hwRxQ08o5ynh/6LapItEQSK4JxVGdAgMBAAECggEBA" +
            "LV59VWHozldNGtUWeCMTKrzQxmb3fIT/uqm17p3Ski0L640Us/3wAHL8Fq8jxUYVtzeLduYQfOFcQ7dsInqYeID7zA6R6bXXBqOZEmBm5yKpNZT" +
            "pMS9DxhYiRisSv50ozf5hImz7wvGjFHlUi8NZ3e+aG3Lbc+4TiLajLx0SWaxVEtBeHDzy3MYt4wf0soxHW040rOhk07YMf8g8W6yh7VX6OkkzVs" +
            "UwtKk1iaUKE4xDuDvLOi1f3RwHRC+c4glfcNQ8EkGYupP760HmsdTC//Cl4XMfzQOoeMSH46xVjmWD+tBJ7zooQW6bm8mWCYYc4t/RNw/5nrkXx" +
            "qd73qKEQECgYEA4HoDIncDXRD3gC6idYNEwDy8iC5UolPejHosQpYgG86XRp0NJYQ+JU9kQFj3YNAH2ks+yqzQq6LRaaci6ggpKGyvyBNk7FoFm" +
            "sUCdBi6+oMXq4M4PwfuZ5IygBh1rLM8tQ+qOYI9Lj/2i46+5b2DB3RBWo1FtnQ7LA5Pl55Txl0CgYEA32myHFDxHoScyMjfqSALzOKMAYrZlIGH" +
            "pZC7Lr6H2T/lrcQHAEpVkAZSAqrhKB0W9QwtkWBDAtV0jK75GIsTkVrJtIf2BSLnQ9wuI9qiDoYckyEw7xoV9uBV1Cxg5VoJZ+0I4su2zKE/YnG" +
            "0OEkbg40GMgaJTRlahBEgrYGYhEECgYB0JQ08JuH5pE665uYt8TaAVKyjtX0a5FQw0QHXjf+dA55n7diggbT57wMK/D06vUhi3S3nBdWOCNdbWB" +
            "wLhR9uiBXHaql8VPOzaZ3kXetYtL1pg6J1km/67LzuZDl2muKdODa2PLnVFUlGWhxRmGWUVMV/ybq5NZhsKhdqdoQYDQKBgG2dc1k3UYaStEZDY" +
            "JGfeoq1INJk6OpXP1G5mE2QCCFMm4lNY839qst2fmh2pPBEjY3/wp/QZjCOwJeCBg/HtPsdW1frWYcdn/CIqE7JJ7gOjxiVMWgvGVW+rf3jJEuD" +
            "iJfoEfMM1ozCFNJdTXpMTGaYG9ERqe4dIW8o5CqdKlLBAoGAbx4zaDeq4NFHmLYxOwUoDqOOaFEAOOR2ff1fBk/xoEpR/E2piI87sQs5l+V5L9w" +
            "/TyV/61yQ6rbG/Pvlzzjj1n7/PbLmdSBWBYQjqK8TO3vOcCqsb6duJrcHPxLC48ieZ1ikIZEtRSEz4GrV62qqFNmBe09FrTjaUjo9Dn+NFDo=";
    public static final String AliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArkUo049kqx1Y6mme9Y1pjxNesSZgT" +
            "LLcwdxRDS3JtkmhWgO/SX2xIFqkbmkMspm94iXklqwG5msWL23I5WTXjLHNGdv5mU9cKx64gN9atOsA0sQ38yiInYMd6PPBM4VOdvKyau0purE9" +
            "RQqwKd1O/XTFlD9XDxEz3NiRD6sunLIxaPMMkt2+X7KPXAwYBIL5tymna3+rBnxYIAX2q5KORaYKoOWRK9ER+pMMXpcqNbMdO1ceOeUqx2XzpVZ" +
            "oMlcgRB6BTKG59S+KVso1O9Cxx52lvYqisuei8OnNwmMxK+++psZXmdDuNpUc4OJXdA7Bc0zbwDedtxRJE3zNDONOOwIDAQAB";

    //支付宝转账
    public static final String gateway="https://openapi.alipay.com/gateway.do";//支付宝网关
    //填写自己创建的app的对应参数
    public static AlipayClient alipayClient = new DefaultAlipayClient
            (gateway, AliAPPID, AliAppPrivateKey, "json", CHARSET, AliPayPublicKey,"RSA2");

//  -------------------------------------------------------------------------------
//    高德地图请求秘钥，web服务
    public static final String gdKEY = "eb378313443345c6ae84ef4ac60dd661";
//    根据地名获取高德经纬度Api
    public static final String GET_LNG_LAT_URL = "http://restapi.amap.com/v3/geocode/geo";
//    根据高德经纬度获取地名Api
    public static final String GET_ADDRESS_URL = "http://restapi.amap.com/v3/geocode/regeo";
//    转换至高德经纬度
    public static final String gd_ADDRESS_URL = "http://restapi.amap.com/v3/assistant/coordinate/convert";
//    关键字模糊查询
    public static final String keyWord_URL = "http://restapi.amap.com/v3/place/text";
//    经纬度/关键字 附近地标建筑及地点查询
    public static final String like_keyWord_URL = "http://restapi.amap.com/v3/place/around";
//    IP定位API服务地址
    public static final String ip_URL = "http://restapi.amap.com/v3/ip";
//    轨迹纠偏地址
    public static final String driving_URL = "http://restapi.amap.com/v4/grasproad/driving";
}
