package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.admin.service.CircleOrderService;
import com.zbkj.common.request.OrderSearchRequest;
import com.zbkj.common.request.OrderTabsHeaderRequest;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.PlatformOrderAdminDetailResponse;
import com.zbkj.common.response.PlatformOrderPageResponse;
import com.zbkj.service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CircleOrderServiceImpl implements CircleOrderService {


    @Autowired
    private CircleHomeService circleHomeService;
    @Autowired
    private OrderService orderService;

    /**
     * 圈层端订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformOrderPageResponse> getCircleOrderPage(OrderSearchRequest request) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new PageInfo<>(new ArrayList<PlatformOrderPageResponse>());
        }
        return orderService.getCircleOrderPage(merIdList, request);
    }

    /**
     * 获取圈层端订单各状态数量
     */
    @Override
    public OrderCountItemResponse getCircleOrderStatusNum(OrderTabsHeaderRequest request) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new OrderCountItemResponse();
        }
        return orderService.getCircleOrderStatusNum(merIdList, request);
    }

    /**
     * 订单详情
     * @param orderNo 订单号
     * @return PlatformOrderAdminDetailResponse
     */
    @Override
    public PlatformOrderAdminDetailResponse getCircleOrderInfo(String orderNo) {
        return orderService.platformInfo(orderNo);
    }
}
