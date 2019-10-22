package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.enums.Variables;
import com.yuyue.app.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * 高德地图API
 */

@Slf4j
@RestController
@RequestMapping(value="/gould", produces = "application/json; charset=UTF-8")
public class GouldController extends BaseController {

    /**
     * 根据高德经纬度获取地址信息
     *
     * @param gdLon 高德地图经度
     * @param gdLat 高德地图纬度
     * @return
     */
    public static String getAddressByLonLat(double gdLon, double gdLat) {
        String location = gdLon + "," + gdLat;
        Map<String, String> params = Maps.newHashMap();
        params.put("location", location);

        // Map<String, String> result = new HashMap<>();
        try {
            // 拼装url
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.GET_ADDRESS_URL);
            // 调用高德SDK
            return GouldUtils.doPost(url, params);
            // 解析Json字符串,获取城市名称
            // JSONObject jsonObject = JSON.parseObject(jsonResult);
            // String regeocode = jsonObject.getString("regeocode");
            // JSONObject regeocodeObj = JSON.parseObject(regeocode);
            // String address = regeocodeObj.getString("formatted_address");
            // 组装结果
            // result.put(location, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据地址信息获取高德经纬度
     *
     * @param address 地址信息
     * @return
     */
    public static String getLonLarByAddress(String address) {
        Map<String, String> params = Maps.newHashMap();
        params.put("address", address);

        // Map<String, String> result = new HashMap<>();
        try {
            // 拼装url
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.GET_LNG_LAT_URL);
            // 调用高德地图SDK
            return GouldUtils.doPost(url, params);

            // 解析JSON字符串,取到高德经纬度
            // JSONObject jsonObject = JSON.parseObject(jsonResult);
            // JSONArray geocodes = jsonObject.getJSONArray("geocodes");
            // String geocode = JSON.toJSONString(geocodes.get(0));
            // JSONObject geocodeObj = JSON.parseObject(geocode);
            // String lonAndLat = geocodeObj.getString("location");
            // 组装结果
            // result.put(address, lonAndLat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //-------------------------------------------------------------------------------------
    /**
     * 阿里云api 根据经纬度获取地址
     *
     * @param log
     * @param lat
     * @return
     */
    public static String getAdd(String log, String lat) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(Variables.gdKEY).append("&location=").append(log).append(",").append(lat);
        String res = HttpUtils.sendPost("http://restapi.amap.com/v3/geocode/regeo", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.getString("regeocode"));
        String add = jsonObject1.get("formatted_address").toString();
        return add;
    }

    /**
     * 阿里云api 根据经纬度获取所在城市
     *
     * @param log
     * @param lat
     * @return
     */
    public static String getCity(String log, String lat) {
        // log 大 lat 小
        // 参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l=" + lat + "," + log + "&type=010";
        String res = "";
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream
                    (), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("addrList"));
            JSONObject j_2 = JSONObject.parseObject(jsonArray.get(0).toString());
            String allAdd = j_2.getString("admName");
            String arr[] = allAdd.split(",");
            res = arr[1];
        } catch (Exception e) {
            logger.info("error in wapaction,and e is " + e.getMessage());
        }
        logger.info(res);
        return res;
    }

    /**
     * 高德api 根据地址获取经纬度
     *
     * @param name
     * @return
     */
    public static String getLatAndLogByName(String name) {
        StringBuffer s = new StringBuffer();
        s.append("key=" + Variables.gdKEY + "&address=" + name);
        String res = HttpUtils.sendPost("http://restapi.amap.com/v3/geocode/geo", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("geocodes"));
        JSONObject location = (JSONObject) jsonArray.get(0);
        String add = location.get("location").toString();
        return add;
    }

    /**
     * 高德api 根据地址获取经纬度
     *
     * @return
     */
    public static String getAddByAMAP(String log, String lat) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(Variables.gdKEY).append("&location=").append(log).append(",").append(lat);
        String res = HttpUtils.sendPost("http://restapi.amap.com/v3/geocode/regeo", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.getString("regeocode"));
        String add = jsonObject1.get("formatted_address").toString();
        return add;
    }


    /**
     * 高德api 坐标转换---转换至高德经纬度
     *
     * @return
     */
    public static String convertLocations(String log, String lat, String type) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(Variables.gdKEY).append("&locations=").append(log).append(",").append(lat).append("&coordsys=");
        if (type == null) {
            s.append("gps");
        } else {
            s.append(type);
        }
        String res = HttpUtils.sendPost("http://restapi.amap.com/v3/assistant/coordinate/convert", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        String add = jsonObject.get("locations").toString();
        return add;
    }


    public static String getAddByName(String name) {
        // log 大 lat 小
        // 参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/geocoding?a=" + name;
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String lon = jsonObject.getString("lon");
            String lat = jsonObject.getString("lat");
            System.err.println(jsonObject);
            res = getNearbyAdd(lon, lat);
        } catch (Exception e) {
            logger.info("error in wapaction,and e is " + e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    public static String getNearbyAdd(String log, String lat) {

        String add = HttpUtils.sendGet("http://ditu.amap.com/service/regeo",
                "longitude=" + log +
                        "&latitude=" + lat + "&type=010");
        logger.info(add);
        return add;
    }

    /**
     * 高德api 关键字模糊查询
     *
     * @param keyWord
     * @param city
     * @return
     */
    public static String getKeywordsAddByLbs(String keyWord, String city) {
        StringBuffer s = new StringBuffer();
        s.append("key=" + Variables.gdKEY + "&keywords=");
        if (keyWord.contains(" ")) {
            String[] str = keyWord.split(" ");
            for (int i = 0; i < str.length; i++) {
                if (i == 0) {
                    s.append(str[i]);
                } else {
                    s.append("+" + str[i]);
                }
            }
        } else {
            s.append(keyWord);
        }
        s.append("&city=" + city);
        s.append("offset=10&page=1");
        String around = HttpUtils.sendPost("http://restapi.amap.com/v3/place/text", s.toString());
        logger.info(around);
        return around;
    }
    /**
     * 高德api 经纬度/关键字 附近地标建筑及地点查询
     *
     * @param log
     * @param lat
     * @param keyWord
     * @return
     */
    public static String getAroundAddByLbs(String log, String lat, String keyWord) {
        String around = HttpUtils.sendPost("http://restapi.amap.com/v3/place/around",
                "key=" + Variables.gdKEY + "&location=" + log + "," + lat + "&keywords=" + keyWord +
                        "&radius=2000&offset=10&page=1");
        logger.info(around);
        return around;
    }
}
