package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.SystemConfig;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigMapper extends MyBaseMapper<SystemConfig> {

    @Select("SELECT *,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i:%s') createTime from yuyue_system_config  WHERE id = 'AHDLHALHFLAHDLAHFLDLFALFJDLAJFJ9'")
    SystemConfig getAdvertisementFeeInfo();
}
