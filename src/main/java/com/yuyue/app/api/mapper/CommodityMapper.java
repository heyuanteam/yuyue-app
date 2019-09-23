package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Advertisement;
import com.yuyue.app.api.domain.Commodity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommodityMapper extends MyBaseMapper<Advertisement> {
    @Transactional
    @Insert("replace INTO yuyue_commodity (\n" +
            "\tCOMMODITY_ID,\n" +
            "\tPRICE_ID," +
            "\tCATEGORY,\n" +
            "\tCOMMODITY_NAME,\n" +
            "\tAD_WORD,\n" +
            "\tAD_IMAGE_URL,\n" +
            "\tCOMMODITY_PRICE,\n" +
            "\tPAY_URL,\n" +
            "\tADDR,\n" +
            "\tSPOKESPERSON_ID,\n" +
            "\tMERCHANT_ID\n" +
            ")  VALUES \n" +
            "(#{commodityId},#{priceId},#{category},#{commodityName},#{adWord},#{adImageUrl},#{commodityPrice}," +
            "#{payUrl},#{addr},#{spokesPersonId},#{merchantId})")
    void commodityToSpread(Commodity commodity);


    List<Advertisement> getCommodityInfo(@Param("merchantId")String merchantId,@Param("videoId")String videoId);

}
