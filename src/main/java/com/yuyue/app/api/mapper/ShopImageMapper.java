package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.ShopImage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShopImageMapper extends MyBaseMapper<ShopImage> {

    @Select("SELECT * FROM yuyue_mall_shop_images WHERE shop_id = #{shopId} ORDER BY image_sort ")
    ShopImage getShopImage(@Param(value = "shopId") String shopId);

    @Transactional
    @Insert("insert  into yuyue_mall_shop_images (image_id,image_path,shop_id,image_sort) " +
            "VALUES" +
            " (#{imageId},#{imagePath},#{shopId},#{imageSort})")
    void insertShopImage(ShopImage shopImage);
}
