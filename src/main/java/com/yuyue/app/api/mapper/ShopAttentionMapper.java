package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.ShopAttention;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopAttentionMapper extends MyBaseMapper<ShopAttention> {

    List<ShopAttention> getShopAttentions(@Param(value = "userId") String userId,@Param(value = "shopId") String shopId);

    void addShopAttention(ShopAttention shopAttention);

    void cancelShopAttention(@Param(value = "userId") String userId,@Param(value = "shopId") String shopId);
}
