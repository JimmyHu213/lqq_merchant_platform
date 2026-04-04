package com.zbkj.service.service.impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.response.ShoppingCreditsRefundOrderPageResponse;
import com.zbkj.service.dao.ShoppingCreditsRefundOrderDao;
import com.zbkj.service.service.ShoppingCreditsRefundOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author HZW
 * @description ShoppingCreditsRefundOrderServiceImpl 接口实现
 * @date 2026-01-08
 */
@Service
public class ShoppingCreditsRefundOrderServiceImpl extends ServiceImpl<ShoppingCreditsRefundOrderDao, ShoppingCreditsRefundOrder> implements ShoppingCreditsRefundOrderService {

    @Resource
    private ShoppingCreditsRefundOrderDao dao;


    @Override
    public List<ShoppingCreditsRefundOrder> findByUserIdAndMerId(Integer userId, Integer merId) {
        LambdaQueryWrapper<ShoppingCreditsRefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingCreditsRefundOrder::getUid, userId);
        lqw.eq(ShoppingCreditsRefundOrder::getMerId, merId);
        lqw.orderByDesc(ShoppingCreditsRefundOrder::getId);
        return dao.selectList(lqw);
    }

    @Override
    public List<ShoppingCreditsRefundOrderPageResponse> findPageByMerchant(HashMap<String, Object> map) {
        return dao.findPageByMerchant(map);
    }

    @Override
    public ShoppingCreditsRefundOrder getByRefundOrderNo(String refundOrderNo) {
        LambdaQueryWrapper<ShoppingCreditsRefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingCreditsRefundOrder::getRefundOrderNo, refundOrderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 审核失败
     */
    @Override
    public Boolean auditRefuse(String refundOrderNo, String reason, Integer auditId, Integer auditType) {
        LambdaUpdateWrapper<ShoppingCreditsRefundOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ShoppingCreditsRefundOrder::getRefundStatus, 1);
        wrapper.set(ShoppingCreditsRefundOrder::getRefusingRefundReason, reason);
        wrapper.set(ShoppingCreditsRefundOrder::getAuditId, auditId);
        wrapper.set(ShoppingCreditsRefundOrder::getAuditType, auditType);
        wrapper.set(ShoppingCreditsRefundOrder::getAuditTime, DateUtil.date());
        wrapper.eq(ShoppingCreditsRefundOrder::getRefundOrderNo, refundOrderNo);
        return update(wrapper);
    }
}

