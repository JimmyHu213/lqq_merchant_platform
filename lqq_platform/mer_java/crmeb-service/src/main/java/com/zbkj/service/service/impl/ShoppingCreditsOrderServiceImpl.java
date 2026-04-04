package com.zbkj.service.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import com.zbkj.common.response.ShoppingCreditsOrderPageResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.dao.ShoppingCreditsOrderDao;
import com.zbkj.service.service.ShoppingCreditsOrderService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.UserShoppingCreditsRecordService;
import com.zbkj.service.service.UserShoppingCreditsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @author HZW
 * @description ShoppingCreditsOrderServiceImpl 接口实现
 * @date 2025-12-04
 */
@Slf4j
@Service
public class ShoppingCreditsOrderServiceImpl extends ServiceImpl<ShoppingCreditsOrderDao, ShoppingCreditsOrder> implements ShoppingCreditsOrderService {

    @Resource
    private ShoppingCreditsOrderDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserShoppingCreditsRecordService userShoppingCreditsRecordService;
    @Autowired
    private UserShoppingCreditsService userShoppingCreditsService;

    /**
     * 获取购物金订单分页列表
     */
    @Override
    public List<ShoppingCreditsOrderPageResponse> findPageByMerchant(HashMap<String, Object> map) {
        return dao.findPageByMerchant(map);
    }

    @Override
    public ShoppingCreditsOrder getByOrderNo(String orderNo) {
        LambdaQueryWrapper<ShoppingCreditsOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingCreditsOrder::getOrderNo, orderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    @Override
    public Boolean paySuccessAfter(ShoppingCreditsOrder shoppingCreditsOrder) {
        User user = userService.getById(shoppingCreditsOrder.getUid());
        UserShoppingCreditsRecord record = new UserShoppingCreditsRecord();
        record.setUid(shoppingCreditsOrder.getUid());
        record.setMerId(shoppingCreditsOrder.getMerId());
        record.setLinkNo(shoppingCreditsOrder.getOrderNo());
        record.setLinkType("recharge");
        record.setType(1);
        record.setTitle("购物金充值");
        record.setRechargeAmount(shoppingCreditsOrder.getRechargeAmount());
        record.setGiftAmount(shoppingCreditsOrder.getGiftAmount());
        Boolean execute = transactionTemplate.execute(e -> {
            // 订单变动
            boolean updatePaid = updatePaid(shoppingCreditsOrder.getId(), shoppingCreditsOrder.getOrderNo());
            if (!updatePaid) {
                log.error("购物金订单更新支付状态失败，orderNo = {}", shoppingCreditsOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            // 购物金变动
            userShoppingCreditsService.updateAmount(user.getId(), shoppingCreditsOrder.getMerId(), shoppingCreditsOrder.getRechargeAmount(), shoppingCreditsOrder.getGiftAmount(), Constants.OPERATION_TYPE_ADD);
            // 创建购物金记录
            userShoppingCreditsRecordService.save(record);
            return Boolean.TRUE;
        });
        if (execute) {
            // TODO 发送购物金购买成功通知
//            asyncService.sendRechargeSuccessNotification(rechargeOrder, user);
        }
        return execute;
    }

    /**
     * 获取已支付订单分页列表
     */
    @Override
    public List<ShoppingCreditsOrder> findPaidPageByMerIdAndUserId(Integer merId, Integer userId) {
        LambdaQueryWrapper<ShoppingCreditsOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingCreditsOrder::getMerId, merId);
        lqw.eq(ShoppingCreditsOrder::getUid, userId);
        lqw.eq(ShoppingCreditsOrder::getPaid, 1);
        lqw.orderByDesc(ShoppingCreditsOrder::getPayTime);
        return dao.selectList(lqw);
    }

    /**
     * 更新退款状态
     */
    @Override
    public Boolean updateRefundStatus(String orderNo, Integer refundStatus) {
        LambdaUpdateWrapper<ShoppingCreditsOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ShoppingCreditsOrder::getRefundStatus, refundStatus);
        wrapper.eq(ShoppingCreditsOrder::getOrderNo, orderNo);
        return update(wrapper);
    }

    @Override
    public ShoppingCreditsOrder getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<ShoppingCreditsOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingCreditsOrder::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取充值人数
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    @Override
    public Integer getRechargeCountByMerIdAndDateGroupUserId(Integer merId, String startDateTime, String endDateTime) {
        Integer rechargeCount = dao.getRechargeCountByMerIdAndDateGroupUserId(merId, startDateTime, endDateTime);
        return ObjectUtil.isNotNull(rechargeCount) ? rechargeCount : 0;
    }

    /**
     * 获取充值金额
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    @Override
    public BigDecimal getRechargeAmountByMerIdAndDate(Integer merId, String startDateTime, String endDateTime) {
        BigDecimal rechargeAmount = dao.getRechargeAmountByMerIdAndDate(merId, startDateTime, endDateTime);
        return ObjectUtil.isNotNull(rechargeAmount) ? rechargeAmount : new BigDecimal("0.00");
    }

    /**
     * 获取充值订单数
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    @Override
    public Integer getRechargeOrderNumByMerIdAndDate(Integer merId, String startDateTime, String endDateTime) {
        Integer rechargeOrderNum = dao.getRechargeOrderNumByMerIdAndDate(merId, startDateTime, endDateTime);
        return ObjectUtil.isNotNull(rechargeOrderNum) ? rechargeOrderNum : 0;
    }

    private Boolean updatePaid(Integer id, String orderNo) {
        LambdaUpdateWrapper<ShoppingCreditsOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ShoppingCreditsOrder::getPaid, true);
        wrapper.set(ShoppingCreditsOrder::getPayTime, CrmebDateUtil.nowDateTime());
        wrapper.eq(ShoppingCreditsOrder::getId, id);
        wrapper.eq(ShoppingCreditsOrder::getOrderNo, orderNo);
        wrapper.eq(ShoppingCreditsOrder::getPaid, false);
        return update(wrapper);
    }
}

