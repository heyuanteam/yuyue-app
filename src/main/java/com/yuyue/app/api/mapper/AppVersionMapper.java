package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.AppVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppVersionMapper extends MyBaseMapper<AppVersion> {

    //查询APP版本，是否需要更新
    AppVersion getAppVersion(@Param("appVersion") String appVersion);
}
