package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.enums.Variables;
import com.yuyue.app.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @ResponseBody
    @RequestMapping("/getAddressByLonLat")
    public JSONObject getAddressByLonLat(String gdLon, String gdLat,HttpServletRequest request, HttpServletResponse response){
        log.info("根据高德经纬度获取地址信息-------------->>/gould/getAddressByLonLat");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();

        String location = gdLon + "," + gdLat;
        Map<String, String> params = Maps.newHashMap();
        params.put("location", location);
        try {
            // 拼装url
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.GET_ADDRESS_URL);
            // 调用高德SDK
            GouldUtils.doPost(url, params);
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
    @ResponseBody
    @RequestMapping("/getLonLarByAddress")
    public JSONObject getLonLarByAddress(String address,HttpServletRequest request, HttpServletResponse response){
        log.info("根据地址信息获取高德经纬度-------------->>/gould/getLonLarByAddress");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();

        Map<String, String> params = Maps.newHashMap();
        params.put("address", address);
        try {
            // 拼装url
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.GET_LNG_LAT_URL);
            // 调用高德地图SDK
            String s = GouldUtils.doPost(url, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 高德api 坐标转换---转换至高德经纬度
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/convertLocations")
    public JSONObject convertLocations(String gdLon, String gdLat, String type,HttpServletRequest request, HttpServletResponse response){
        log.info("高德api 坐标转换---转换至高德经纬度-------------->>/gould/convertLocations");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();

        StringBuffer s = new StringBuffer();
        s.append("key=").append(Variables.gdKEY).append("&locations=").append(gdLon).append(",").append(gdLat).append("&coordsys=");
        if (type == null) {
            s.append("gps");
        } else {
            s.append(type);
        }
//        String res = HttpUtils.sendPost("http://restapi.amap.com/v3/assistant/coordinate/convert", s.toString());
//        log.info(res);
//        JSONObject jsonObject = JSONObject.parseObject(res);
//        String add = jsonObject.get("locations").toString();
        return null;
    }

    /**
     * 高德api 关键字模糊查询
     *
     * @param keyWord
     * @param city
     * @return
     */
    @ResponseBody
    @RequestMapping("/getKeywordsAddByLbs")
    public JSONObject getKeywordsAddByLbs(String keyWord, String city,HttpServletRequest request, HttpServletResponse response){
        log.info("高德api 关键字模糊查询-------------->>/gould/getKeywordsAddByLbs");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();

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
//        String around = HttpUtils.sendPost("http://restapi.amap.com/v3/place/text", s.toString());
//        log.info(around);
        return null;
    }
    /**
     * 高德api 经纬度/关键字 附近地标建筑及地点查询
     *
     * @param keyWord
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAroundAddByLbs")
    public JSONObject getAroundAddByLbs(String gdLon, String gdLat, String keyWord,HttpServletRequest request, HttpServletResponse response){
        log.info("高德api 经纬度/关键字 附近地标建筑及地点查询-------------->>/gould/getAroundAddByLbs");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();

//        String around = HttpUtils.sendPost("http://restapi.amap.com/v3/place/around",
//                "key=" + Variables.gdKEY + "&location=" + gdLon + "," + gdLat
//                        + "&keywords=" + keyWord + "&radius=2000&offset=10&page=1");
//        log.info(around);
        return null;
    }
}
