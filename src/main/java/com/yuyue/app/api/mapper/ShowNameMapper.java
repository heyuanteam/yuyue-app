package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.ShowName;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShowNameMapper extends MyBaseMapper<ShowName> {

    @Select("select *,DATE_FORMAT(CREATE_TIME ,'%Y-%m-%d %H:%i:%s') createTime from yuyue_show_name where userId =#{id}")
    ShowName getShowInfo(String id);

    @Select("select * from yuyue_show_name where description =#{description} and phone =#{phone} and teamName =#{teamName} LIMIT 1")
    ShowName findShowName(@Param("description")String description,@Param("phone") String phone,@Param("teamName") String teamName);

    @Transactional
    @Insert("INSERT INTO yuyue_show_name (id,userId,teamName,size,address,categoryId,description,phone,videoAddress,imageAddress,mail,weChat)" +
            " VALUES (#{id},#{userId},#{teamName},#{size},#{address},#{categoryId},#{description},#{phone},#{videoAddress},#{imageAddress},#{mail},#{weChat})")
    void insertShowName(@Param("id") String id, @Param("userId") String userId,@Param("teamName")  String teamName,
                        @Param("size")  String size,@Param("address") String address,
                        @Param("categoryId") String categoryId,@Param("description") String description,
                        @Param("phone")String phone,
                        @Param("videoAddress")  String videoAddress,@Param("imageAddress")  String imageAddress,
                        @Param("mail") String mail,@Param("weChat") String weChat);
}
