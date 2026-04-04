package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.admin.service.CircleRefundOrderService;
import com.zbkj.common.request.RefundOrderSearchRequest;
import com.zbkj.common.response.PlatformRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderAdminDetailResponse;
import com.zbkj.common.response.RefundOrderCountItemResponse;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CircleRefundOrderServiceImpl implements CircleRefundOrderService {

    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private CircleHomeService circleHomeService;


    /**
     * 圈层端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformRefundOrderPageResponse> getCircleRefundOrderPage(RefundOrderSearchRequest request) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new PageInfo<>(new ArrayList<PlatformRefundOrderPageResponse>());
        }
        return refundOrderService.getCircleRefundOrderPage(request, merIdList);
    }

    /**
     * 获取圈层端退款订单各状态数量
     */
    @Override
    public RefundOrderCountItemResponse getCircleRefundOrderStatusNum(RefundOrderSearchRequest request) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new RefundOrderCountItemResponse();
        }
        return refundOrderService.getCircleRefundOrderStatusNum(request, merIdList);
    }

    /**
     * 圈层端退款订单详情
     * @param refundOrderNo 退款订单号
     * @return RefundOrderAdminDetailResponse
     */
    @Override
    public RefundOrderAdminDetailResponse getCircleDetail(String refundOrderNo) {
        return refundOrderService.getPlatformDetail(refundOrderNo);
    }
}
