package com.yuyue.app.api.domain;
import java.net.InetSocketAddress;

/**
 * @author: Lucifer
 * @create: 2018-11-11 04:00
 * @description: 定义常用的变量
 **/
public class Variables {

    //安装fastdfs的虚拟机的ip
    public static final String ip_home = "101.37.252.177:8888";

    //组名，跟你在fastdfs配置文件中的一致
    public static final String groupName = "/group1";


    //默认文件格式，后缀名,设置上传后在fastdfs存储的格式，你可以改成其它格式图片，fastdfs只支持几种常用格式的，自己百度可以查查，jpg和png都是可以的
    public static final String fileExtName = "jpg";

    public final static int port = 22122;

    public final static int store_port = 23000;

    public static InetSocketAddress address = new InetSocketAddress(ip_home, port);

    public static InetSocketAddress store_address = new InetSocketAddress(ip_home, store_port);

    //超时时间
    public static final int soTimeout = 550;

    //连接时间
    public static final int connectTimeout = 500;
}
