package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.response.ShoppingCreditsRefundOrderPageResponse;

import java.util.HashMap;
import java.util.List;

/**
 * @author HZW
 * @description ShoppingCreditsRefundOrderService 接口
 * @date 2026-01-08
 */
public interface ShoppingCreditsRefundOrderService extends IService<ShoppingCreditsRefundOrder> {

    List<ShoppingCreditsRefundOrder> findByUserIdAndMerId(Integer userId, Integer merId);

    List<ShoppingCreditsRefundOrderPageResponse> findPageByMerchant(HashMap<String, Object> map);

    ShoppingCreditsRefundOrder getByRefundOrderNo(String refundOrderNo);

    /**
     * 审核失败
     */
    Boolean auditRefuse(String refundOrderNo, String reason, Integer auditId, Integer auditType);
}