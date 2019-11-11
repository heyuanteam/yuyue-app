package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.MallAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallAddressMapper extends MyBaseMapper<MallAddress> {

    @Select("SELECT * FROM yuyue_mall_delivery_address WHERE user_id = #{userId} ORDER BY default_addr DESC ")
    List<MallAddress> getMallAddrByUserId(@Param(value = "userId") String userId);


    @Select("SELECT * FROM yuyue_mall_delivery_address WHERE address_id = #{addressId} limit 1")
    MallAddress getMallAddress(@Param(value = "addressId") String addressId);



    @Transactional
    @Insert("REPLACE INTO yuyue_mall_delivery_address (address_id,user_id,specific_addr,receiver,phone,zip_code,default_addr) " +
            "VALUES (#{addressId},#{userId},#{specificAddr},#{receiver},#{phone},#{zipCode},#{defaultAddr})")
    void editMallAddr(MallAddress mallAddress);

    @Transactional
    @Delete("DELETE FROM yuyue_mall_delivery_address WHERE address_id = #{addressId}")
    void deleteMallAddr(@Param(value = "addressId")String addressId);


    @Transactional
    @Delete("update  yuyue_mall_delivery_address set default_addr = '0' \n" +
            "WHERE user_id = #{userId} and default_addr = '1'")
    void changeDefaultAddr(@Param(value = "userId")String userId);
}
