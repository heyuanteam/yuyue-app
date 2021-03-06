package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Advertisement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AdvertisementMapper extends MyBaseMapper<Advertisement> {

    @Transactional
    @Insert("INSERT INTO yuyue_advertisement_info  (id,userId,merchantAddr,businessLicense,idCardZM,idCardFM,agencyCode," +
            "merchantName,phone,produceAddr,fixedPhone,email,wx,qqNum,merchandiseUrl,telephone) \n" +
            " VALUES (#{id},#{userId},#{merchantAddr},#{businessLicense},#{idCardZM},#{idCardFM},#{agencyCode},#{merchantName},#{phone}," +
            "#{produceAddr},#{fixedPhone},#{email},#{wx},#{qqNum},#{merchandiseUrl},#{telephone})")
    void addAdvertisemenInfo(Advertisement advertisement);

    @Select("SELECT *,DATE_FORMAT(APPLICATION_TIME,'%Y-%m-%d %H:%i:%s') applicationTime," +
                     "DATE_FORMAT(SPREAD_TIME,'%Y-%m-%d %H:%i:%s') spreadTime" +
            " FROM yuyue_advertisement_info WHERE userId = #{userId} ")
    Advertisement getAdvertisementInfo(String userId);

    @Select("SELECT * FROM yuyue_advertisement_info WHERE agencyCode = #{agencyCode} and produceAddr =#{produceAddr} and phone =#{phone} and status = '10A' LIMIT 1 ")
    Advertisement findAdvertisement(@Param("agencyCode") String agencyCode,@Param("produceAddr") String produceAddr,@Param("phone") String phone);
}
