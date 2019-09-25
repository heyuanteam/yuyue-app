package com.yuyue.app.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.Collator;
import java.util.*;

public class MD5Utils {
    private static final String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String getMD5Str(String str) throws Exception {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new Exception("MD5加密出现错误，"+e.toString());
        }
    }

    /**
     * 获取本地文件的md5
     * @param filePath 文件地址
     * @return
     */
    public static String getFileMd5Value(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return "";
        }
        byte[] buffer = new byte[2048];
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            while (true) {
                int len = in.read(buffer, 0, 2048);
                if (len != -1) {
                    digest.update(buffer, 0, len);
                } else {
                    break;
                }
            }
            in.close();

            byte[] md5Bytes = digest.digest();
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 直接获取网络文件的md5值
     * @param urlStr
     * @return
     */
    public static String getMd5ByUrl(String urlStr){
        String md5 = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            DataInputStream in = new DataInputStream(conn.getInputStream());
            md5 = DigestUtils.md5Hex(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return md5;
    }

    public static String signDatashwx(Map SortedMap, String md5Key) {
        List stringSort = new ArrayList();
        Iterator i$ = SortedMap.entrySet().iterator();
        do {
            if (!i$.hasNext())
                break;
            java.util.Map.Entry entry = (java.util.Map.Entry) i$.next();
            if (entry.getValue() != null && !"".equals(entry.getValue()) && !"null".equals(entry.getValue())
                    && !((String) entry.getKey()).equals("sign") && !((String) entry.getKey()).equals("pl_sign"))
                stringSort.add(entry.getKey());
        } while (true);
        Collections.sort(stringSort, Collator.getInstance(Locale.CHINA));
        StringBuffer signDatas = new StringBuffer();
        for (int i = 0; i < stringSort.size(); i++)
            signDatas.append((String) stringSort.get(i)).append("=").append((String) SortedMap.get(stringSort.get(i))).append("&");

//        System.out.println(signDatas.toString());
        String s = signDatas.toString();
        s = s.substring(0, s.length() - 1);
        s = (new StringBuilder()).append(s).append("&key=").append(md5Key).toString();
//        System.out.println("签名之前的数据为----------------------" + s);
        return wxEncode(s.toString()).toUpperCase();
    }

    public static String wxEncode(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = md5.digest(password.getBytes("utf-8"));
            String passwordMD5 = byteArrayToHexString(byteArray);
            return passwordMD5;
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return password;
    }

    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();
        for (byte b : byteArray) {
            sb.append(byteToHexChar(b));
        }
        return sb.toString();
    }

    private static Object byteToHexChar(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hex[d1] + hex[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

}
