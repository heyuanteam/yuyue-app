package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Address;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressMapper extends MyBaseMapper<Address> {

    @Select("SELECT b.id id,b.division_name divisionName,b.parent_id parentId," +
            "b.area_code divisionCode,b.china_region_id chinaRegionId,b.area_code areaCode from yuyue_address b ")
    List<Address> getAddress();
}
