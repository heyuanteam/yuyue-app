package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.ShowName;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShowNameMapper extends MyBaseMapper<ShowName> {

    @Transactional
    @Insert("INSERT INTO yuyue_show_name (id,userId,teamName,size,address,cardZUrl,cardFUrl,categoryId,description,phone,videoAddress,mail,weChat)" +
            " VALUES (#{id},#{userId},#{teamName},#{size},#{address},#{cardZUrl},#{cardFUrl},#{categoryId},#{description},#{phone},#{videoAddress},#{mail},#{weChat})")
    void insertShowName(ShowName showName);
    @Select("select *,DATE_FORMAT(CREATE_TIME ,'%Y-%m-%d %H:%i:%s') createTime from yuyue_show_name where userId =#{id}")
    ShowName getShowInfo(String id);
}
