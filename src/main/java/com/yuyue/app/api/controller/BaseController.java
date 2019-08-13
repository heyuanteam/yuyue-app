package com.yuyue.app.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * controller基类
 */
public class BaseController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Map<String,String> getParameterMap(HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName.trim());
            map.put(propertyName,propertyValue);
        }
        return map;
    }
}
