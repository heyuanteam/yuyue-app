package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Specification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SpecificationMapper extends MyBaseMapper<Specification> {

    @Select("SELECT * FROM yuyue_mall_shop_commodity WHERE shop_id = #{shopId}  ORDER BY create_time DESC")
    List<Specification> getSpecification(@Param(value = "shopId") String shopId);

    @Transactional
    @Insert("insert  into yuyue_mall_shop_commodity (commodity_id,shop_id,commodity_details," +
            "commodity_price,commodity_reserve,image_path,status) \n" +
            "VALUES  \n" +
            " (#{commodityId},#{shopId},#{commodityDetail},#{commodityPrice},#{commodityReserve},#{imagePath},#{status})")
    void insertSpecification(Specification specification);
}
