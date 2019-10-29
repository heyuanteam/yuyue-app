package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Specification;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SpecificationMapper extends MyBaseMapper<Specification> {

    @Select("SELECT * FROM yuyue_mall_shop_commodity WHERE shop_id = #{shopId}  ORDER BY create_time DESC")
    List<Specification> getSpecification(@Param(value = "shopId") String shopId);

    @Select("SELECT * FROM yuyue_mall_shop_commodity WHERE commodity_id = #{specificationId}  limit 1")
    Specification getSpecificationById(@Param(value = "specificationId") String specificationId);

    @Transactional
    @Insert("insert  into yuyue_mall_shop_commodity (commodity_id,shop_id,commodity_detail,commodity_size," +
            "commodity_price,commodity_reserve,image_path,status) \n" +
            "VALUES  \n" +
            " (#{commodityId},#{shopId},#{commodityDetail},#{commoditySize},#{commodityPrice},#{commodityReserve},#{imagePath},#{status})")
    void insertSpecification(Specification specification);

    @Delete("DELETE FROM yuyue_mall_shop_commodity WHERE commodity_id = #{specificationId}")
    void deleteSpecification(String specificationId);

    @Update("update yuyue_mall_shop_commodity   set " +
            "commodity_detail = #{commodityDetail} ," +
            "commodity_size = #{commoditySize} ," +
            "commodity_price = #{commodityPrice}  , " +
            "commodity_reserve = #{commodityReserve} ," +
            "image_path = #{imagePath}," +
            "status = #{status}  where commodity_id = #{commodityId} " )
    void updateSpecification(Specification specification);

}
