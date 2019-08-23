package com.yuyue.app.api.domain;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
    private String userNo;
    private String nickName;
    private String realName;
    private String idType;
    private String idCard;
    private String phone;
    private String email;
    private String password;
    private String salt;
    private BigDecimal total;
    private String sex;
    private String addrDetail;
    private String headpUrl;
    private String userType;
    private String userStatus;
    private String createTime;
    private String updateTime;
    private String attentionTotal;
    private String collectionTotal;
//    学历
    private String education;
//    微信
    private String wechat;
//    个性签名
    private String signature;
//    正面人物图片
    private String userUrl;
//    正面身份证
    private String cardZUrl;
//    反面身份证
    private String cardFUrl;


//    用户id			id            	  string
//    用户NO			userNo			  string
//    用户名			nick_name			string
//    真实姓名		real_name			string
//    证件类型        id_type				string
//    身份证号信息	id_card				string
//    电话		  	phone				Integer
//    邮箱		 	Email				string
//    密码			password			string
//    盐				salt				Integer
//    余额			balance				double
//    性别			sex					string
//    详细地址		address_detail		string
//    头像url			headp_url			string
//    用户类型		（UserType:vip,ordinary ）	string
//    用户权限 		（user_permission：） 		string
//    用户状态  		user_status					string
//    创建时间  		create_time					data
//    关注id			attention_id		string			关注列表（关注人列表）
//    收藏id			collection_id		string			收藏列表（收藏信息）
//    结算id			settlement_id		string			结算列表（结算信息）
//    原创作品id 		works_id			string
//    版本号id		version_id			string

}
