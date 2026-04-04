package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.response.ShoppingCreditsOrderPageResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @author HZW
 * @description ShoppingCreditsOrderService 接口
 * @date 2025-12-04
 */
public interface ShoppingCreditsOrderService extends IService<ShoppingCreditsOrder> {

    /**
     * 获取购物金订单分页列表
     */
    List<ShoppingCreditsOrderPageResponse> findPageByMerchant(HashMap<String, Object> map);

    ShoppingCreditsOrder getByOrderNo(String orderNo);

    Boolean paySuccessAfter(ShoppingCreditsOrder shoppingCreditsOrder);

    /**
     * 获取已支付订单分页列表
     */
    List<ShoppingCreditsOrder> findPaidPageByMerIdAndUserId(Integer merId, Integer userId);

    /**
     * 更新退款状态
     */
    Boolean updateRefundStatus(String orderNo, Integer refundStatus);

    ShoppingCreditsOrder getByOutTradeNo(String outTradeNo);

    /**
     * 获取充值人数
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    Integer getRechargeCountByMerIdAndDateGroupUserId(Integer merId, String startDateTime, String endDateTime);

    /**
     * 获取充值金额
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    BigDecimal getRechargeAmountByMerIdAndDate(Integer merId, String startDateTime, String endDateTime);

    /**
     * 获取充值订单数
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    Integer getRechargeOrderNumByMerIdAndDate(Integer merId, String startDateTime, String endDateTime);
}