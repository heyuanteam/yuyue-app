package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.domain.UserComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BarrageMapper extends MyBaseMapper<Barrage> {

    //TIMES<600    展示当前时间与存储时间小于10分钟的所有弹幕
    @Select(" SELECT * FROM ( SELECT *,ABS(TIMESTAMPDIFF(SECOND,NOW(),DURATION)) as TIMES " +
            "from yuyue_video_barrage WHERE VIDEO_ID = #{videoId} )B WHERE B.TIMES<600  " )
    List<Barrage> getBarrages(String videoId);

    @Transactional
    @Insert("insert into yuyue_video_barrage (ID,VIDEO_ID,TEXT,USER_ID)  values  " +
            "(#{id},#{videoId},#{text},#{userId})")
    void addBarrage(Barrage barrage);

}
