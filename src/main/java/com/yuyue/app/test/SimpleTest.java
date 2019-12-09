package com.yuyue.app.test;

import com.google.common.collect.Maps;
import com.yuyue.app.api.domain.AppUser;
import com.yuyue.app.api.service.LoginService;
import com.yuyue.app.enums.Variables;
import com.yuyue.app.utils.GouldUtils;
import com.yuyue.app.utils.HttpUtils;
import com.yuyue.app.utils.MallUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SimpleTest {



    public static void main(String[] args) {
       /* String a="0.05";
        String b="0.1";
        BigDecimal bigDecimal = new BigDecimal(a).multiply(new BigDecimal(b)).setScale(2,BigDecimal.ROUND_HALF_UP);
        System.out.println(bigDecimal);
        System.out.println((int)5/3);

        Date javaUtilCurrentTime = new Date();
        System.out.println(javaUtilCurrentTime);
        String time1="2019-11-06 19:00:00";
        String time2="2019-11-06 21:00:00";
        String[] split1 = time1.split(" ");
        String[] split2 = time2.split(" ");
        time1.split(" ");
        int res=time1.compareTo(time2);
        System.out.println(res);
        int s1 = Integer.parseInt(split1[1].split(":")[0]);
        int s2 = Integer.parseInt(split2[1].split(":")[0]);
        System.out.println(s1-s2);
        System.out.println();
        String format = new SimpleDateFormat("HH").format(new Date());
        System.out.println(format);
        System.out.println("_______"+split2[1]+"_______"+split1[1]);

        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            System.out.println(path+"------"+path.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        /*String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        System.out.println("path"+path);
        String url=System.getProperty("user.dir");
        System.out.println("url"+url);
        File upload1 =null;
        try {
            String path1 = ResourceUtils.getURL("classpath:").getPath();
            System.out.println("path1"+path1);
             upload1 = new File(path1,"static/images1");
            if(!upload1.exists()){
                upload1.mkdirs();
                System.out.println("----------");
                System.out.println("upload1 url:"+upload1.getAbsolutePath());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("upload1 getAbsolutePath:"+upload1.getAbsolutePath());
        System.out.println("upload1 getParent:"+upload1.getPath());
        try {
            System.out.println("upload1 getCanonicalPath:"+upload1.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File upload= new File(path,"static/images");
        if(!upload.exists()){
            upload.mkdirs();
            System.out.println("----------");
        }
        System.out.println("upload url:"+upload.getAbsolutePath());*/
        /*String idCard="412726199410042431";
        System.getProperty("user.dir").replace("bin", "webapps/qrcode_image");
        //Pattern pattern = Pattern.compile(" ^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
        //Pattern pattern = Pattern.compile("\\\\d{15}(\\\\d{2}[0-9xX])?");
        Pattern pattern = Pattern.compile("^\\d{6}(18|19|20)?\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|[xX])$");


        if (pattern.matcher(idCard).matches() == false || idCard.length() != 18) {
            System.out.println("错误");
        }else System.out.println("正确");*/
        /*RedisTemplate<String, String> stringTemplate = new RedisTemplate<>();*/

        /*String s="娱悦用户"+ RandomSaltUtil.randomNumber(8);
        System.out.println(s);*/
        /*Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-29 13:37:20");
            endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-04-24 13:37:20");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean isStart = new Date().after(startDate);
        boolean notEnd = new Date().before(endDate);

        if (isStart &&  notEnd) System.out.println("发布中");
        else if (notEnd ==false) System.out.println("已过期");
        else System.out.println("未发布");*/

        /*String a="112";
        String b="123.23";
        String c="123a";
        if (a.matches("[0-9]+")){
            System.out.println("a:是整数"+a);
        }else {
            System.out.println("a:不是整数"+a);
        }if (b.matches("[0-9]+")){
            System.out.println("b:是整数"+b);
        }else {
            System.out.println("b:不是整数"+b);
        }if (c.matches("[0-9]+")){
            System.out.println("c：是整数"+c);
        }else {
            System.out.println("c:不是整数"+c);
        }*/

       /* List<String> list = Lists.newArrayList();
        List<String> list1 = Lists.newArrayList();
        list1.add("123");
        list.addAll(list1);
        System.out.println(list.get(0));*/


//        String [] strs= new String[]{
//                "B044C53B38BA4E84B507E62402683E26",
//                "4D42B0ED17C64653AD31FD402858FBBB",
//                "C7EC5FA6E0964CC9AD14222D780F6AEB",
//                "A408C597F8A140F68D10C21CCDFF2AF3",
//                "F38607A875A44CF78D84176B545282B7"};
//        for (String s : strs) {
//            System.out.println(Math.abs(s.hashCode()) % 10+"");
//        }

//         double d = 4.145;
//         d = (double) Math.round(d * 10) / 10;
//         System.out.println(d);
//        String cartStr = "FBE391F5D4C04D5DB60F9ADF79F6AA94:5-5FF99665F69C4CE7B33669876395BB7C:4";
//        if (cartStr.contains("-")){
//            String[] cartStrings = cartStr.split("-");
//            for (String s:cartStrings
//                 ) {
//                System.out.println(s);
//                String[] split = s.split(":");
//                System.out.println(split[0]+ split[1]);
//            }
//        }else {
//            System.out.println(cartStr);
//            String[] split = cartStr.split(":");
//            System.out.println(split[0]+ split[1]);
//        }
        /*    634543A9414EFDBEB63B6BDDB8535D11[488DA0232479449D9FE0571FA4FFB984:2]-
              A0E34543A9414EFDBEB63B6BDDB8156[5FF99665F69C4CE7B33669876395BB7C:1;
              F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]*/
//        String s = "A0E34543A9414EFDBEB63B6BDDB8156[5FF99665F69C4CE7B33669876395BB7C:1;F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]";
//        String shopId = s.substring(0, s.indexOf("["));
//        String commodityIds = s.substring( s.indexOf("[")+1,s.lastIndexOf("]"));
//        String[] commodityInfos = commodityIds.split(";");
//        for (String commodityInfo:commodityInfos
//             ) {
//            System.out.println(commodityInfo);
//            String commodityId = commodityInfo.split(":")[0];
//            String commodityNum = commodityInfo.split(":")[1];
//            System.out.println(commodityId+":    "+commodityNum);
//
//        }
//        System.out.println(shopId);
//        System.out.println(commodityIds);
//        String feeArea = "西藏省:15;新疆省:22;内蒙古:18;青海:30";
//
//        String specificAddr = "河南-杭州市-萧山区杭州湾信息港";
//        String substring = specificAddr.substring(0, specificAddr.indexOf("-"));
//        System.out.println(substring);
//        if (feeArea.contains(substring)){
//            if (feeArea.contains(";")){
//                String[] split = feeArea.split(";");
//                for (String s:split
//                     ) {
//                    if (s.contains(substring)){
//                        String fare = s.split(":")[1];
//                        System.out.println("     -------"+fare);
//                        System.out.println("匹配成功！");
//                    }
//                }
//            }else {
//                if (feeArea.contains(substring)){
//                    String fare = feeArea.split(":")[1];
//                    System.out.println("匹配成功！");
//                }else {
//                    System.out.println("匹配失败！");
//                }
//
//            }
//
//
//        }
//        BigDecimal getFare = new BigDecimal(0);
//        if (getFare.compareTo(new BigDecimal(-1)) > 0){
//            System.out.println(true);
//        }else System.out.println(false);
//        int i = 0;
//            if (i <= Integer.parseInt("0")){
//            System.out.println("dui");
//        }else{
//                System.out.println("cuo");
//            }
//        String s = "5FF99665F69C4CE7B33669876395BB7C:1;F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1";
//        String[] commodityInfos = s.split(";");
//        for (int i =0 ;i < commodityInfos.length ;i++){
//            System.out.println(commodityInfos.length);
//        }
//        String tradeType = "SCZFB";
//        if(!"SCWX".equals(tradeType) || !"SCZFB".equals(tradeType)){
//            System.out.println("false");
//        }else {
//            System.out.println("true");
//        }
//        String cartStr = "A0E34543A9414EFDBEB63B6BDDB8156[5FF99665F69C4CE7B33669876395BB7C:1]";
//        Map<String, String> stringStringMap = MallUtils.splitCartString(cartStr);
//        for (String key:stringStringMap.keySet()
//             ) {
//            String s = stringStringMap.get(key);
//            System.out.println(key+s);
//        }


//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
//        String time1 = "08:00";
//        String time2 = "18:00";

//        System.out.println("----------");
//        Date startDate = null;
//        Date endDate = null;
//        try {
//            startDate = new SimpleDateFormat("hh:mm:ss").parse("13:37:00");
//            endDate = new SimpleDateFormat("hh:mm:ss").parse("23:37:00");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        boolean isStart = new Date().after(startDate);
//        boolean notEnd = new Date().before(endDate);
//        if (isStart &&  notEnd){
//            System.out.println("发布中");
//        }
//        else if (notEnd ==false || isStart ==false) {
//            System.out.println("已过期");
//        }

//        String s = "123";
//        String a = "a";
//        String b = "b";
//        if ("123".equals(s) && ("b".equals(a) || "b".equals(a))){
//            System.out.println("~~~~~");
//        }else {
//            System.out.println("-----");
//        }
//        String cartStr = "8ABC88451CCD4A5B94E484C3D49D5B81[78B9FE2E0406476E8EF132C218F6CC0D:3]-8ABC88451CCD4A5B94E484C3D49D5B87[A529E05D78734079BA9B226C38B11E6C:1]";
//        List<String> shopIds = new ArrayList<>();
//        if (cartStr.contains("-")) {
//            String[] splits = cartStr.split("-");
//            for (String split : splits
//            ) {
//                String shopId = split.substring(0, split.indexOf("["));
//                if ("8ABC88451CCD4A5B94E484C3D49D5B81".equals(shopId)){
////                    cartStr.substring("8ABC88451CCD4A5B94E484C3D49D5B81",)
//                    String newStr = null;
//
//                    System.out.println("---"+newStr);
//                }
//            }
//        }else {
//
//        }

//        String cartStr = "8ABC88451CCD4A5B94E484C3D49D5B81[78B9FE2E0406476E8EF132C218F6CC0D:3]-8ABC88451CCD4A5B94E484C3D49D5B87[A529E05D78734079BA9B226C38B11E6C:1]-";
//        Map<String, String> stringStringMap = MallUtils.splitCartString(cartStr);
//        for ( String key:stringStringMap.keySet()
//             ) {
//            String commodityIds = cartStr.substring( cartStr.indexOf("[")+1,cartStr.lastIndexOf("]"));
//            System.out.println("dfg"+commodityIds);
//            System.out.println("key:"+key+"      value:"+stringStringMap.get(key));
//        }
//        List<String> shopIds = MallUtils.getShopIds(cartStr);
//        for (String s:shopIds
//             ) {
//            if ("8ABC88451CCD4A5B94E484C3D49D5B81".equals(s)){
//                String newStr = cartStr.substring(cartStr.indexOf("8ABC88451CCD4A5B94E484C3D49D5B81"),cartStr.indexOf("["));
//                System.out.println(newStr);
//                System.out.println(cartStr);
//            }
//            System.out.println("shop:"+s);
//        }
//        String distanceId = "不能";
//        if(!distanceId.matches("[1-8]")){
//            System.out.println("+++++++");
//        }
//
//        else if (Integer.parseInt(distanceId)<=8 && Integer.parseInt(distanceId)>=1){
//            System.out.println("------");
//        }

    }

}
