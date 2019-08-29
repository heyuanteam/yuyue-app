package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.AppVersion;
import com.yuyue.app.api.domain.ShowName;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShowNameMapper extends MyBaseMapper<ShowName> {

    @Transactional
    @Insert("INSERT INTO yuyue_show_name (id,userId,teamName,size,address,cardZUrl,cardFUrl,categoryId,description,phone,videoAddress,mail,weChat)" +
            " VALUES (#{id},#{userId},#{teamName},#{size},#{address},#{cardZUrl},#{cardFUrl},#{categoryId},#{description},#{phone},#{videoAddress},#{mail},#{weChat})")
    void insertShowName(ShowName showName);
}
