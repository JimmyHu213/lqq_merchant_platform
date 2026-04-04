package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.RefundOrderSearchRequest;
import com.zbkj.common.response.PlatformRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderAdminDetailResponse;
import com.zbkj.common.response.RefundOrderCountItemResponse;

public interface CircleRefundOrderService {


    /**
     * 圈层端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<PlatformRefundOrderPageResponse> getCircleRefundOrderPage(RefundOrderSearchRequest request);

    /**
     * 获取圈层端退款订单各状态数量
     */
    RefundOrderCountItemResponse getCircleRefundOrderStatusNum(RefundOrderSearchRequest request);

    /**
     * 圈层端退款订单详情
     * @param refundOrderNo 退款订单号
     * @return RefundOrderAdminDetailResponse
     */
    RefundOrderAdminDetailResponse getCircleDetail(String refundOrderNo);
}
