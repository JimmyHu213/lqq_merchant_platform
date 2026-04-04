package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.merchant.MerchantMember;
import com.zbkj.common.model.user.UserShoppingCredits;
import com.zbkj.service.dao.UserShoppingCreditsDao;
import com.zbkj.service.service.UserShoppingCreditsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author HZW
 * @description UserShoppingCreditsServiceImpl 接口实现
 * @date 2026-01-12
 */
@Service
public class UserShoppingCreditsServiceImpl extends ServiceImpl<UserShoppingCreditsDao, UserShoppingCredits> implements UserShoppingCreditsService {

    @Resource
    private UserShoppingCreditsDao dao;


    @Override
    public UserShoppingCredits getByUserIdAndMerId(Integer userId, Integer merId) {
        LambdaQueryWrapper<UserShoppingCredits> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserShoppingCredits::getUid, userId);
        lqw.eq(UserShoppingCredits::getMerId, merId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

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
    @Override
    public Boolean updateAmount(Integer userId, Integer merId, BigDecimal rechargeAmount, BigDecimal giftAmount, String type) {
        UpdateWrapper<UserShoppingCredits> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("recharge_amount = recharge_amount + {}", rechargeAmount));
            wrapper.setSql(StrUtil.format("gift_amount = gift_amount + {}", giftAmount));
        } else {
            wrapper.setSql(StrUtil.format("recharge_amount = recharge_amount - {}", rechargeAmount));
            wrapper.setSql(StrUtil.format("gift_amount = gift_amount - {}", giftAmount));
        }
        wrapper.eq("uid", userId);
        wrapper.eq("mer_id", merId);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" recharge_amount - {} >= 0 and gift_amount - {} >= 0", rechargeAmount, giftAmount));
        }
        return update(wrapper);
    }

    /**
     * 获取累计充值金额
     */
    @Override
    public BigDecimal getTotalRechargeAmount(Integer userId, Integer merId) {
        BigDecimal amount = dao.getTotalRechargeAmount(userId, merId);
        return ObjectUtil.isNotNull(amount) ? amount : new BigDecimal("0.00");
    }

    /**
     * 获取累计赠送金额
     */
    @Override
    public BigDecimal getTotalGiftAmount(Integer userId, Integer merId) {
        BigDecimal amount = dao.getTotalGiftAmount(userId, merId);
        return ObjectUtil.isNotNull(amount) ? amount : new BigDecimal("0.00");
    }

    /**
     * 获取累计消费金额
     */
    @Override
    public BigDecimal getTotalConsumeAmount(Integer userId, Integer merId) {
        BigDecimal amount = dao.getTotalConsumeAmount(userId, merId);
        return ObjectUtil.isNotNull(amount) ? amount : new BigDecimal("0.00");
    }
}

