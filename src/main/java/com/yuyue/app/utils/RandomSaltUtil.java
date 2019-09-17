package com.yuyue.app.utils;


import java.util.Random;

/**
 * 类的作用：随机生成时间戳
 * 作者：gzy
 * 创建时间：2015年8月3日下午4:14:27
 */
public class RandomSaltUtil {
    /**
     *
     * 方法作用：生成四个字符时间戳
     */
    public static StringBuilder sb=null;
    public static String generetRandomSaltCode(int number){
        //字符串转char数组
        char[] str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        sb = new StringBuilder();
        Random random=new Random();
        for(int i=0;i<number;i++){
            //随机生成0到str长度之间的数字做为下标
            int randomIndex=random.nextInt(str.length);
            //追加到sb对象
            sb.append(str[randomIndex]);
        }
        return sb.toString().toUpperCase();
    }


    /*生成随机数*/
    public static String randomNumber(int number){
        sb=new StringBuilder();
        int[] a={0,1,2,3,4,5,6,7,8,9};
        Random random=new Random();
        for (int i=0;i<number;i++){
            int randomIndex=random.nextInt(a.length);
            //追加到sb对象
            sb.append(a[randomIndex]);
        }
        return sb.toString();
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
    String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    int number = 0;
    for (int i = 0; i < length; i++) {
        number = random.nextInt(base.length());
        sb.append(base.charAt(number));
    }
        return sb.toString();
    }
}
