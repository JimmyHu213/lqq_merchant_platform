package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.user.UserShoppingCredits;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户购物金表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2026-01-12
 */
public interface UserShoppingCreditsDao extends BaseMapper<UserShoppingCredits> {

    BigDecimal getTotalRechargeAmount(@Param("userId") Integer userId, @Param("merId") Integer merId);

    BigDecimal getTotalGiftAmount(@Param("userId") Integer userId, @Param("merId") Integer merId);

    BigDecimal getTotalConsumeAmount(@Param("userId") Integer userId, @Param("merId") Integer merId);
}
