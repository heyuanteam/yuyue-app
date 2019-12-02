package com.yuyue.app.utils;

import java.util.*;

public class MallUtils {
/*634543A9414EFDBEB63B6BDDB8535D11[488DA0232479449D9FE0571FA4FFB984:2]-
        A0E34543A9414EFDBEB63B6BDDB8156[5FF99665F69C4CE7B33669876395BB7C:1;
        F2F6F78CE342413AA20C4968F1BCED0A:1;FBE391F5D4C04D5DB60F9ADF79F6AA94:1]*/


    public static Map<String,String> splitCartString(String cartStr){
        Map<String,String> map = new TreeMap<>();

        if (cartStr.contains("-")) {
            String[] split = cartStr.split("-");
            for (String shop:split
                 ) {
                String commodityIds = shop.substring( shop.indexOf("[")+1,shop.lastIndexOf("]"));
                if (commodityIds.contains(";")){
                    String[] specificationInfos = commodityIds.split(";");
                    for (String specificationInfo:specificationInfos
                         ) {
                        String[] specification = specificationInfo.split(":");
                        map.put(specification[0],specification[1]);
                    }

                }else {
                    String[] specification = commodityIds.split(":");
                    map.put(specification[0],specification[1]);
                }
            }
        }else {
            String commodityIds = cartStr.substring( cartStr.indexOf("[")+1,cartStr.lastIndexOf("]"));
            if (commodityIds.contains(";")){
                String[] specificationInfos = commodityIds.split(";");
                for (String specificationInfo: specificationInfos
                     ) {
                    String[] specification = specificationInfo.split(":");
                    map.put(specification[0],specification[1]);
                }
            }else {
                String[] specification = commodityIds.split(":");
                map.put(specification[0],specification[1]);
            }


        }
        return map;
    }

    /*
    * 8ABC88451CCD4A5B94E484C3D49D5B87[78B9FE2E0406476E8EF132C218F6CC0D:3]
    * -8ABC88451CCD4A5B94E484C3D49D5B87[A529E05D78734079BA9B226C38B11E6C:1]
    * */
    public static Map<String,String> getShopIds(String cartStr) {
       Map<String,String> map = new HashMap<>();
        if (cartStr.contains("-")) {
            String[] splits = cartStr.split("-");
            for (String split : splits
            ) {
                String shopId = split.substring(0, split.indexOf("["));
                String commodityIds = split.substring( split.indexOf("[")+1,split.lastIndexOf("]"));
                map.put(shopId,commodityIds);
            }
        }else {
            String shopId = cartStr.substring(0, cartStr.indexOf("["));
            String commodityIds = cartStr.substring( cartStr.indexOf("[")+1,cartStr.lastIndexOf("]"));
            map.put(shopId,commodityIds);
        }
        return map;
    }

}
