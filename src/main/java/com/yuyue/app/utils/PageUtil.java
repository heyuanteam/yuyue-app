package com.yuyue.app.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

/**
 * 创建人     : sanri
 * 创建时间   : 2018/9/1-15:12
 * 功能       : list 拆分
 */
@Data
public class PageUtil<T> implements Iterator<List<T>> {
    protected List<T> list;
    private int position;
    //默认每页 1000 条
    private int pageSize = 10;
//    //当前页
//    private int pageNum = 1;
//    //总页数
//    private int pages = 1;
//    //总条数
//    private long total = 0L;

    public PageUtil(List<T> originData,int pages){
        if (StringUtils.isEmpty(originData)) {
            return;
        }
        this.list = originData;
        position = 0;       //初始化位置为 0
    }

//    public PageUtil(List<T> originData, int pageSize) {
//        this(originData);
//        if(pageSize <= 0){
//            throw new IllegalArgumentException("pageSize 需要输入一个大于 0 的值");
//        }
//        this.pageSize = pageSize;
//    }

    @Override
    public boolean hasNext() {
        return list != null && !list.isEmpty() && position < list.size();
    }

    @Override
    public List<T> next() {
        if(hasNext()){
            int start = position;
            int end = position + pageSize ;
            if(end >= list.size()){
                end = list.size();
            }
            List<T> current = list.subList(start,end);
            position = end ;
            return current;
        }
        return null;
    }

    public int size(){
        return list == null ? 0 : ( list.size() - 1) / pageSize  + 1;
    }

    public List<T> get(int index){
        if(list == null) return null;

        int start = index * pageSize;
        int end = (index + 1 ) * pageSize;
        if (end >= list.size()){
            end = list.size();
        }
        return list.subList(start,end);
    }

    /**
     * 获取当前位置
     * @return
     */
    public long position() {
        return position;
    }
}

