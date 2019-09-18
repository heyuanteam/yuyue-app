package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Barrage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BarrageMapper extends MyBaseMapper<Barrage> {

    //TIMES<600    展示当前时间与存储时间小于10分钟的所有弹幕
/*    @Select(" SELECT * FROM ( SELECT *,ABS(TIMESTAMPDIFF(SECOND,NOW(),DURATION)) as TIMES " +
            "from yuyue_video_barrage WHERE VIDEO_ID = #{videoId} )B WHERE B.TIMES<600  " )*/

    @Select("SELECT\n" +
            "\tBARRAGE_ID barrageId,\n" +
            "\tUSER_ID userId,\n" +
            "\tTEXT text,\n" +
            "\tTIME_POINT timePoint,\n" +
            "\tVIDEO_ID videoId,\n" +
            "\tDATE_FORMAT(CREATE_TIME,'%Y-%m-%d %H:%i:%s') createTime,\n" +
            "\tUSER_NAME userName,\n" +
            "\tUSER_HEAD_URL userHeadUrl\n" +
            "FROM\n" +
            "\tyuyue_video_barrage\n" +
            "WHERE\n" +
            "\tTIME_POINT BETWEEN #{startTime}\n" +
            "AND #{endTime}\n" +
            "ORDER BY\n" +
            "\tTIME_POINT")
    List<Barrage> getBarrages(@Param(value = "videoId") String videoId, @Param(value = "startTime")  int startTime, @Param(value = "endTime") int endTime);

    @Transactional
    @Insert("replace into yuyue_video_barrage " +
            "(BARRAGE_ID,USER_ID,VIDEO_ID,USER_NAME,USER_HEAD_URL,TEXT,TIME_POINT)  " +
            "values  " +
            "(#{barrageId},#{userId},#{videoId},#{userName},#{userHeadUrl},#{text},#{timePoint})")
    void addBarrage(Barrage barrage);

}
