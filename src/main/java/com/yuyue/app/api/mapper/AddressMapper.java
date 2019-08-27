package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Address;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressMapper extends MyBaseMapper<Address> {

    @Select("SELECT * from yuyue_address ")
    List<Address> getAddress();
}
