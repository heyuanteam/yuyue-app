package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.ShopImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ShopImageMapper extends MyBaseMapper<ShopImage> {

    @Select("SELECT * FROM yuyue_mall_shop_images WHERE shop_id = #{shopId} ORDER BY image_sort ")
    List<ShopImage> getShopImage(@Param(value = "shopId") String shopId);

    @Transactional
    @Insert("REPLACE into yuyue_mall_shop_images (id,image_path,shop_id,image_sort,original_image)  \n" +
            "VALUES \n" +
            " (#{id},#{imagePath},#{shopId},#{imageSort},#{originalImage})")
    void insertShopImage(ShopImage shopImage);

    @Transactional
    @Delete("DELETE FROM  yuyue_mall_shop_images  WHERE  image_path = #{imagePath} " )
    void deleteShopImage(@Param(value = "imagePath")String imagePath);

    @Transactional
    @Delete("DELETE FROM  yuyue_mall_shop_images  WHERE  shop_id = #{shopId} " )
    void deleteImageByShopId(@Param(value = "shopId")String shopId);


}
