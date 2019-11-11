package com.yuyue.app.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author ly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
    //用户No
    private String userNo;
    //网名
    private String nickName;
    //真实姓名
    private String realName;
    //身份验证类型
    private String idType;
    //身份证号
    private String idCard;
    //电话
    private String phone;
//    定位城市
    private String city;
//    极光别名
    private String jpushName;
    //用户密码
    private String password;
    //盐
    private String salt;
    //余额
    private BigDecimal total;
    //收益
    private BigDecimal income;
    //性别
    private String sex;
    //住址
    private String addrDetail;
    //头像
    private String headpUrl;
    //用户类型
    private String userType;
    //用户状态
    private String userStatus;
    //创建时间
    private String createTime;
    //更新时间
    private String updateTime;
//    用户关注数量(粉丝量)
    private String attentionTotal;
//    点赞量
    private String likeTotal;
//    评论数量
    private String commentTotal;
//    学历
    private String education;
//    微信
    private String wechat;
//    个性签名
    private String signature;
//    用户正面照
    private String userUrl;
//    正面身份证
    private String cardZUrl;
//    反面身份证
    private String cardFUrl;
//    微信转账标识
    private String opendId;
//    微信转账名称
    private String wechatName;
//    上级手机号
    private String fatherPhone;

    //艺人封面
    private String frontCover;
//    用户上传的视频   一对多
    private List<UploadFile> authorVideo;



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
