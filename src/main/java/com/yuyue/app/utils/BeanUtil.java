package com.yuyue.app.utils;


import com.yuyue.app.annotation.MyException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class BeanUtil extends BeanUtils {

    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);


    /**
     * 拷贝对象属性到目标对象 <p> <b>示例</b> BeanSource bs = new BeanSource(); bs.set(...); ... BeanTarget bt =
     * BeanUtil.copyProperties(bs,BeanTarget.class);
     *
     * </p>
     *
     * @param sourceObj 源对象
     * @param targetClazz 目标类
     * @return 目标对象
     */
    public static <T> T copyProperties(Object sourceObj, Class<T> targetClazz) {
        return BeanUtil.copyProperties(sourceObj, targetClazz, new String[]{});
    }

    private static <T> T copyProperties(Object sourceObj, Class<T> targetClazz,
                                        String... ignoreProperties) {
        if (sourceObj == null) {
            return null;
        }

        T targetObj = null;
        try {
            targetObj = targetClazz.newInstance();
            BeanUtils.copyProperties(sourceObj, targetObj, ignoreProperties);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("对象转换错误,sourceObj={},targetClazz={},ignoreProperties={}", sourceObj, targetClazz,
                    ignoreProperties, e);
            throw new MyException("对象转换错误" + e.getMessage(), e);
        }
        return targetObj;
    }

    /**
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreNullFlag 是否忽略null值
     */
    public static void copyProperties(Object source, Object target, Boolean ignoreNullFlag) {
        if (ignoreNullFlag) {
            copyPropertiesNotNull(source, target, ArrayUtils.EMPTY_STRING_ARRAY);
        } else {
            copyProperties(source, target);
        }
    }

    /**
     * 拷贝源列表对象到目标列表
     *
     * @param sourceList 源列表
     * @param targetClazz 目标列表元类型
     * @return 目标列表
     */
    public static <T> List<T> copyBeans(List<?> sourceList, Class<T> targetClazz) {
        return BeanUtil.copyBeans(sourceList, targetClazz, new String[]{});
    }

    public static <T> List<T> copyBeans(List<?> sourceList, Class<T> targetClazz,
                                        String... ignoreProperties) {
        if (sourceList == null) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<>();
        for (Object sourceObj : sourceList) {
            T targetObj = BeanUtil.copyProperties(sourceObj, targetClazz, ignoreProperties);
            targetList.add(targetObj);
        }
        return targetList;
    }

    public static void copyPermittedProperties(Object source, Object target,
                                               String... permitProperties) {
        copyPermitedProperties(source, target, null, permitProperties);
    }

    private static void copyPermitedProperties(Object source, Object target, Class<?> editable,
                                               String... permitProperties) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> permitList = (permitProperties != null ? Arrays.asList(permitProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();

            if (writeMethod == null || (permitList != null && !permitList.contains(targetPd.getName()))) {
                continue;
            }

            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
            if (sourcePd != null) {
                Method readMethod = sourcePd.getReadMethod();
                if (readMethod != null &&
                        ClassUtils
                                .isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                    try {
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source);
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        writeMethod.invoke(target, value);
                    } catch (Exception ex) {
                        throw new FatalBeanException(
                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                    }
                }
            }
        }
    }

    public static void copyPropertiesNotNull(Object source, Object target,
                                             String... ignoreProperties) {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null
                    && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                            readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (value != null) {// 只拷贝不为null的属性
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        } catch (Exception ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
}
