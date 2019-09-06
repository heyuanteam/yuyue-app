package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Advertisement;
import com.yuyue.app.api.domain.Commodity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommodityMapper extends MyBaseMapper<Advertisement> {

    @Insert("INSERT INTO yuyue_commodity (\n" +
            "\tCOMMODITY_ID,\n" +
            "\tCATEGORY,\n" +
            "\tCOMMODITY_NAME,\n" +
            "\tAD_WORD,\n" +
            "\tAD_URL,\n" +
            "\tCOMMODITY_PRICE,\n" +
            "\tAD_DURATION," +
            "\tPAY_URL,\n" +
            "\tAD_PRICE,\n" +
            "\tADDR,\n" +
            "\tSPOKESPERSON_ID,\n" +
            "\tMERCHANT_ID\n" +
            ")  VALUES \n" +
            "(#{commodityId},#{category},#{commodityName},#{adWord},#{adUrl},#{commodityPrice}," +
            "#{adDuration},#{payUrl},#{adPrice},#{addr},#{spokesPersonId},#{merchantId})")
    void commodityToSpread(Commodity commodity);


    List<Advertisement> getCommodityInfo(@Param("merchantId")String merchantId,@Param("spokesPersonId")String spokesPersonId);

}
