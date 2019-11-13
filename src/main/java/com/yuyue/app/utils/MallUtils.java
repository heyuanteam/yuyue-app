package com.yuyue.app.utils;

import java.util.Map;
import java.util.TreeMap;

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
}
