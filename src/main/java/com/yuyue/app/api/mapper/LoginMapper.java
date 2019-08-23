package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.AppUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ly
 */
@Repository
public interface LoginMapper extends MyBaseMapper<AppUser> {

    AppUser getAppUserMsg(@Param("phone") String phone,@Param("id") String id);

    @Transactional
    @Insert("insert into yuyue_merchant (ID,USER_NO,USER_NICK_NAME,USER_REAL_NAME,PHONE,PASSWORD,SALT) " +
            "values (#{id},#{userNo},#{nickName},#{realName},#{phone},#{password},#{salt})")
    void addUser(AppUser appUser);

    @Transactional
    @Update("UPDATE yuyue_merchant b SET b.`PASSWORD` = #{password} WHERE b.PHONE = #{phone}")
    void editPassword(@Param("phone") String phone,@Param("password") String password);
}
