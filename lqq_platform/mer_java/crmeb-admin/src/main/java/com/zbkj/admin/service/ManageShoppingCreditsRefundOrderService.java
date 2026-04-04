package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.admin.request.ShoppingCreditsRefundOrderSearchRequest;
import com.zbkj.admin.response.ShoppingCreditsRefundOrderInfoResponse;
import com.zbkj.common.request.OrderRefundAuditRequest;
import com.zbkj.common.response.ShoppingCreditsRefundOrderPageResponse;

/**
 * 管理端购物金退款订单服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/5
 */
public interface ManageShoppingCreditsRefundOrderService {

    /**
     * 购物金退款订单分页列表
     */
    PageInfo<ShoppingCreditsRefundOrderPageResponse> findPage(ShoppingCreditsRefundOrderSearchRequest request);

    /**
     * 退款单详情-商户端
     *
     * @param refundOrderNo 退款单号
     */
    ShoppingCreditsRefundOrderInfoResponse getInfoByMerchant(String refundOrderNo);

    Boolean audit(OrderRefundAuditRequest request);
}
