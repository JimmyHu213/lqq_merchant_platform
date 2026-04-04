package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.OrderSearchRequest;
import com.zbkj.common.request.OrderTabsHeaderRequest;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.PlatformOrderAdminDetailResponse;
import com.zbkj.common.response.PlatformOrderPageResponse;

public interface CircleOrderService {


    /**
     * 圈层端订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<PlatformOrderPageResponse> getCircleOrderPage(OrderSearchRequest request);

    /**
     * 获取圈层端订单各状态数量
     */
    OrderCountItemResponse getCircleOrderStatusNum(OrderTabsHeaderRequest request);

    /**
     * 圈层端订单详情
     * @param orderNo 订单号
     * @return PlatformOrderAdminDetailResponse
     */
    PlatformOrderAdminDetailResponse getCircleOrderInfo(String orderNo);
}
