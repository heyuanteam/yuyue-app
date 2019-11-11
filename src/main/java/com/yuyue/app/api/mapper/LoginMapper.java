package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.AppUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ly
 */
@Repository
public interface LoginMapper extends MyBaseMapper<AppUser> {

    AppUser getAppUserMsg(@Param("password") String password,@Param("phone") String phone,@Param("id") String id,
                          @Param("idCard") String idCard);

    @Transactional
    @Insert("INSERT into yuyue_merchant (ID,USER_NO,USER_NICK_NAME,USER_REAL_NAME,PHONE,PASSWORD,SALT) " +
            "values (#{id},#{userNo},#{nickName},#{realName},#{phone},#{password},#{salt})")
    void addUser(AppUser appUser);

    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.`PASSWORD` = #{password} WHERE b.PHONE = #{phone}")
    void editPassword(@Param("phone") String phone,@Param("password") String password);

    @Transactional
    void updateAppUser(@Param("id") String id, @Param("nickName") String nickName,@Param("realName") String realName,
                       @Param("idCard") String idCard,@Param("phone") String phone, @Param("sex") String sex,
                       @Param("headpUrl") String headpUrl,@Param("userStatus") String userStatus, @Param("addrDetail") String addrDetail,
                       @Param("education") String education,@Param("wechat") String wechat,@Param("signature") String signature,
                       @Param("userUrl") String userUrl,@Param("cardZUrl") String cardZUrl,@Param("cardFUrl") String cardFUrl,
                       @Param("password") String password,@Param("city") String city,@Param("jpushName") String jpushName,
                       @Param("opendId") String opendId,@Param("wechatName") String wechatName,@Param("frontCover") String frontCover);

    /**
     * 模糊查询   用户关注搜索
     * @param userId
     * @param content
     * @return
     */
    List<AppUser> getAppUserMsgToLike(@Param("userId")String userId,@Param("content")String content);

    /**
     * 用户认证
     * @param userId
     * @param realName
     * @param idCard
     * @param userUrl
     * @param cardZUrl
     * @param cardFUrl
     */
    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.`USER_REAL_NAME` = #{realName}," +
            "b.`ID_CARD` = #{idCard},b.`userUrl` = #{userUrl}," +
            "b.`cardZUrl` = #{cardZUrl},b.cardFUrl=#{cardFUrl}," +
            "b.`USER_STATUS` ='10B'" +
            " WHERE b.id = #{userId}")
    void userAuthentication(@Param(value = "userId") String userId,@Param(value = "realName")String realName,
                            @Param(value = "idCard")String idCard,@Param(value = "userUrl")String userUrl,
                            @Param(value = "cardZUrl")String cardZUrl, @Param(value = "cardFUrl")String cardFUrl);
    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.`opendId` = #{opendId},b.wechatName=#{wechatName} WHERE b.id = #{id}")
    void updateOpendId(@Param("id") String id,@Param("opendId") String opendId,@Param("wechatName") String wechatName);

    List<AppUser> getAppUserByFatherPhone(@Param("fatherPhone") String fatherPhone);
}
