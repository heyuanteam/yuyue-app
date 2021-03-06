package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PayMapper extends MyBaseMapper<Order> {

    @Transactional
    @Insert("INSERT into yuyue_order (id,orderNo,tradeType,money,mobile,status,merchantId,note,sourceId)  values  " +
            "(#{id},#{orderNo},#{tradeType},#{money},#{mobile},#{status},#{merchantId},#{note},#{sourceId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void createOrder(Order order);

    @Select("SELECT *,DATE_FORMAT(COMPLETE_TIME ,'%Y-%m-%d %H:%i:%s') completeTime FROM yuyue_order b WHERE b.id = #{orderId} limit 1")
    Order getOrderId(String orderId);

    List<Order> getGGOrder(@Param(value = "consumerId") String consumerId,@Param(value = "status") String status);


    List<Order> getSCOrder(@Param(value = "consumerId") String consumerId,@Param(value = "status") String status);

    @Transactional
    @Update("UPDATE yuyue_order b SET b.`status` = #{status} WHERE b.id = #{id} ")
    void updateStatus( @Param("id")String id,@Param("status") String status);

    @Transactional
    @Update("UPDATE yuyue_order b SET b.`status`=#{status},b.responseCode=#{responseCode},b.responseMessage=#{responseMessage} " +
            "WHERE b.orderNo = #{orderNo} ")
    void updateOrderStatus(@Param("responseCode") String responseCode,@Param("responseMessage") String responseMessage,
                           @Param("status") String status, @Param("orderNo") String orderNo);

    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.TOTAL = #{money} WHERE b.ID = #{merchantId} ")
    void updateTotal(@Param("merchantId") String merchantId,@Param("money") BigDecimal money);

    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.income = #{money} WHERE b.ID = #{merchantId}")
    void updateOutIncome(@Param("merchantId") String merchantId,@Param("money") BigDecimal money);

    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.mIncome = #{money} WHERE b.ID = #{merchantId}")
    void updateMIncome(@Param("merchantId") String merchantId,@Param("money") BigDecimal money);

    @Select("SELECT *,DATE_FORMAT(COMPLETE_TIME ,'%Y-%m-%d %H:%i:%s') completeTime FROM yuyue_order b " +
            " WHERE b.merchantId = #{id} AND b.`status` = '10B' order by b.COMPLETE_TIME desc LIMIT #{begin},#{size}")
    List<Order> getMoneyList(@Param("id") String id,@Param(value = "begin") int begin,@Param(value = "size")int size);

    @Transactional
    @Insert("INSERT into yuyue_out_money (id,outNo,tradeType,money,merchantId,responseCode,responseMessage,moneyNumber,realName)  values  " +
            " (#{id},#{outNo},#{tradeType},#{money},#{merchantId},#{responseCode},#{responseMessage},#{moneyNumber},#{realName})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void createOut(OutMoney outMoney);

    @Transactional
    @Update("UPDATE yuyue_out_money b SET b.`status`=#{status},b.responseCode=#{responseCode},b.responseMessage=#{responseMessage} "
            +" WHERE b.outNo = #{outNo} ")
    void updateOutStatus(@Param("responseCode") String responseCode,@Param("responseMessage") String responseMessage,
                         @Param("status") String status,@Param("outNo") String outNo);

    @Select("SELECT *,DATE_FORMAT(COMPLETE_TIME ,'%Y-%m-%d %H:%i:%s') completeTime FROM yuyue_out_money b WHERE b.merchantId = #{id} LIMIT #{begin},#{size}")
    List<OutMoney> getOutMoneyList(@Param("id") String id,@Param(value = "begin") int begin,@Param(value = "size")int size);

    @Select("SELECT * FROM yuyue_gift")
    List<Gift> getGiftList();

    @Select("SELECT * FROM yuyue_gift where id= #{id} LIMIT 1 ")
    Gift getGift(@Param("id") String id);

    @Transactional
    @Insert("INSERT into yuyue_change_money (id,changeNo,tradeType,money,merchantId,mobile,note,sourceId,videoId,status,moneyNumber,realName,historyMoney)  values  " +
            " (#{id},#{changeNo},#{tradeType},#{money},#{merchantId},#{mobile},#{note},#{sourceId},#{videoId},#{status},#{moneyNumber},#{realName},#{historyMoney})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void createShouMoney(ChangeMoney changeMoney);

    List<ChangeMoneyVo> changeMoneyList(@Param(value = "userId")String userId,@Param(value = "videoId")String videoId,
                                        @Param(value = "tradeType")String tradeType,
                                        @Param(value = "begin") int begin,@Param(value = "size")int size);

    @Select("SELECT * FROM yuyue_order b where DATE_FORMAT(b.COMPLETE_TIME ,'%Y-%m-%d %H:%i:%s') < #{startTime} AND b.`status` = '10A'")
    List<Order> findOrderList(@Param(value = "startTime") String startTime);

    ChangeMoney getChangeMoney(@Param(value = "id")String id,@Param(value = "orderItemId") String orderItemId);

    @Transactional
    void updateChangeMoneyStatus(@Param("subtract") BigDecimal subtract,@Param(value = "responseCode")String responseCode,@Param(value = "responseMessage") String responseMessage,
                                 @Param(value = "status")String status,@Param(value = "id") String id);

    List<String> getShopUserList(@Param(value = "id")String id);

    String getMoneyStatus(@Param(value = "orderId") String orderId);

    ChangeMoney getChangeMoneyByTime(@Param(value = "userId") String userId);
}
