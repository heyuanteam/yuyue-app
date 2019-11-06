package com.yuyue.app.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuyue.app.enums.ReturnResult;
import com.yuyue.app.enums.Variables;
import com.yuyue.app.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Map;

/**
 * 高德地图API
 */

@Slf4j
@RestController
@RequestMapping(value="/gould", produces = "application/json; charset=UTF-8")
public class GouldController extends BaseController {

    /**
     * 根据高德经纬度获取地址信息
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
        Map<String, String> params = Maps.newHashMap();
        params.put("location", gdLon + "," + gdLat);
        try {
            // 拼装url
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.GET_ADDRESS_URL);
            // 调用高德SDK
            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPost(url, params));
            if ("OK".equals(parse.getString("info"))) {
                returnResult.setMessage("获取地址信息成功！");
                returnResult.setResult(parse);
                returnResult.setStatus(Boolean.TRUE);
            } else {
                returnResult.setMessage("获取地址信息失败！");
            }
        } catch (Exception e) {
            log.info("根据高德经纬度获取地址信息失败！");
            returnResult.setMessage("获取地址信息失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 根据地址信息获取高德经纬度
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
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.GET_LNG_LAT_URL);
            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPost(url, params));
            if ("OK".equals(parse.getString("info"))) {
                returnResult.setMessage("获取高德经纬度成功！");
                returnResult.setResult(parse);
                returnResult.setStatus(Boolean.TRUE);
            } else {
                returnResult.setMessage("获取高德经纬度失败！");
            }
        } catch (Exception e) {
            log.info("获取高德经纬度失败！");
            returnResult.setMessage("获取高德经纬度失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 高德api 坐标转换---转换至高德经纬度
     * @return
     */
    @ResponseBody
    @RequestMapping("/convertLocations")
    public JSONObject convertLocations(String gdLon, String gdLat, String type,HttpServletRequest request, HttpServletResponse response){
        log.info("高德api 坐标转换---转换至高德经纬度-------------->>/gould/convertLocations");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        Map<String, String> params = Maps.newHashMap();
        if (StringUtils.isEmpty(type)) {
            type = "gps";
        }
        try {
            params.put("locations", gdLon+ "," +gdLat);
            params.put("coordsys", type);
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.gd_ADDRESS_URL);
            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPost(url, params));
            if ("OK".equals(parse.getString("info"))) {
                returnResult.setMessage("转换至高德经纬度成功！");
                returnResult.setResult(parse);
                returnResult.setStatus(Boolean.TRUE);
            } else {
                returnResult.setMessage("转换至高德经纬度失败！");
            }
        } catch (Exception e) {
            log.info("转换至高德经纬度失败！");
            returnResult.setMessage("转换至高德经纬度失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 高德api 关键字模糊查询
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
        Map<String, String> params = Maps.newHashMap();
        try {
            //同时存在，以城市为准！
            params.put("keywords", GouldUtils.getKeyWord(keyWord));
            params.put("city", city);
            params.put("offset", "10");
            params.put("page", "1");
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.keyWord_URL);
            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPost(url, params));
            if ("OK".equals(parse.getString("info"))) {
                returnResult.setMessage("关键字模糊查询成功！");
                returnResult.setResult(parse);
                returnResult.setStatus(Boolean.TRUE);
            } else {
                returnResult.setMessage("关键字模糊查询失败！");
            }
        } catch (Exception e) {
            log.info("关键字模糊查询失败！");
            returnResult.setMessage("关键字模糊查询失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 高德api 经纬度/关键字 附近地标建筑及地点查询
     * @param keyWord
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAroundAddByLbs")
    public JSONObject getAroundAddByLbs(String gdLon, String gdLat, String keyWord,HttpServletRequest request, HttpServletResponse response){
        log.info("高德api 经纬度/关键字 附近地标建筑及地点查询-------------->>/gould/getAroundAddByLbs");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        Map<String, String> params = Maps.newHashMap();
        try {
            String location = "";
            if (StringUtils.isNotEmpty(gdLon) && StringUtils.isNotEmpty(gdLat)) {
                location = gdLon + "," + gdLat;
            }
            if (StringUtils.isEmpty(gdLon) || StringUtils.isEmpty(gdLat)) {
                returnResult.setMessage("经纬度/关键字不对！");
                return ResultJSONUtils.getJSONObjectBean(returnResult);
            }
            params.put("location", location);
            params.put("keywords", GouldUtils.getKeyWord(keyWord));
            params.put("radius", "2000");
            params.put("offset", "10");
            params.put("page", "1");
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.like_keyWord_URL);
            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPost(url, params));
            if ("OK".equals(parse.getString("info"))) {
                returnResult.setMessage("经纬度/关键字 附近地标建筑及地点查询成功！");
                returnResult.setResult(parse);
                returnResult.setStatus(Boolean.TRUE);
            } else {
                returnResult.setMessage("经纬度/关键字 附近地标建筑及地点查询失败！");
            }
        } catch (Exception e) {
            log.info("经纬度/关键字 附近地标建筑及地点查询失败！");
            returnResult.setMessage("经纬度/关键字 附近地标建筑及地点查询失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 根据本机IP获取地址
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAddressByIP")
    public JSONObject getAddressByIP(HttpServletRequest request, HttpServletResponse response){
        log.info("根据本机IP获取地址-------------->>/gould/getAddressByIP");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        Map<String, String> params = Maps.newHashMap();
        params.put("ip", HttpUtils.getIpAddress(request,response));
        try {
            String url = GouldUtils.jointUrl(params, Variables.OUTPUT, Variables.gdKEY, Variables.ip_URL);
            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPost(url, params));
            if ("OK".equals(parse.getString("info"))) {
                returnResult.setMessage("根据本机IP获取地址成功！");
                returnResult.setResult(parse);
                returnResult.setStatus(Boolean.TRUE);
            } else {
                returnResult.setMessage("根据本机IP获取地址失败！");
            }
        } catch (Exception e) {
            log.info("根据本机IP获取地址失败！");
            returnResult.setMessage("根据本机IP获取地址失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

    /**
     * 轨迹纠偏
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAddressByDriving")
    public JSONObject getAddressByDriving(BigDecimal gdLon, BigDecimal gdLat,HttpServletRequest request, HttpServletResponse response){
        log.info("轨迹纠偏-------------->>/gould/getAddressByDriving");
        getParameterMap(request, response);
        ReturnResult returnResult=new ReturnResult();
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNull(gdLon) || StringUtils.isNull(gdLat)) {
            returnResult.setMessage("经纬度/关键字不对！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        params.put("x", gdLon);//经度
        params.put("y", gdLat);//纬度
        params.put("sp", 4);
        params.put("ag", 110);
        params.put("tm", System.currentTimeMillis());
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(params);
        try {
            String url = GouldUtils.jointUrl(jsonArray, Variables.OUTPUT, Variables.gdKEY, Variables.driving_URL);
//            JSONObject parse = (JSONObject)JSON.parse(GouldUtils.doPosts(url, params));
//            if ("OK".equals(parse.getString("info"))) {
//                returnResult.setMessage("轨迹纠偏成功！");
//                returnResult.setResult(parse);
//                returnResult.setStatus(Boolean.TRUE);
//            } else {
//                returnResult.setMessage("轨迹纠偏失败！");
//            }
        } catch (Exception e) {
            log.info("轨迹纠偏失败！");
            returnResult.setMessage("轨迹纠偏失败！");
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }

}
