package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.user.UserShoppingCredits;

import java.math.BigDecimal;

/**
 * @author HZW
 * @description UserShoppingCreditsService 接口
 * @date 2026-01-12
 */
public interface UserShoppingCreditsService extends IService<UserShoppingCredits> {

    UserShoppingCredits getByUserIdAndMerId(Integer userId, Integer merId);

    /**
     * 更新商户会员金额
     *
     * @param userId         用户ID
     * @param merId          商户ID
     * @param rechargeAmount 充值金额
     * @param giftAmount     赠送金额
     * @param type           增加add、扣减sub
     * @return Boolean
     */
    Boolean updateAmount(Integer userId, Integer merId, BigDecimal rechargeAmount, BigDecimal giftAmount, String type);

    /**
     * 获取累计充值金额
     */
    BigDecimal getTotalRechargeAmount(Integer userId, Integer merId);

    /**
     * 获取累计赠送金额
     */
    BigDecimal getTotalGiftAmount(Integer userId, Integer merId);

    /**
     * 获取累计消费金额
     */
    BigDecimal getTotalConsumeAmount(Integer userId, Integer merId);
}