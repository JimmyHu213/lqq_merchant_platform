package com.zbkj.admin.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.ManageOrderService;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.ReservationWorkOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ManageOrderServiceImpl implements ManageOrderService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ReservationWorkOrderService reservationWorkOrderService;

    /**
     * 商户端后台分页列表
     *
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantOrderPageResponse> getMerchantAdminPage(OrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.getMerchantAdminPage(request, systemAdmin);
    }

    /**
     * 获取商户端订单各状态数量
     */
    @Override
    public OrderCountItemResponse getMerchantOrderStatusNum(OrderTabsHeaderRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.getMerchantOrderStatusNum(request, systemAdmin);
    }

    /**
     * 订单详情（PC）
     *
     * @param orderNo 订单编号
     * @return OrderAdminDetailResponse
     */
    @Override
    public OrderAdminDetailResponse adminDetail(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.adminDetail(orderNo, systemAdmin);
    }

    /**
     * 发货
     *
     * @param request 发货参数
     * @return Boolean
     */
    @Override
    public Boolean send(OrderSendRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        // 拆单发货
        return orderService.send(request, systemAdmin);
    }

    /**
     * 小票打印
     *
     * @param orderNo 订单编号
     * @return 打印结果
     */
    @Override
    public void printReceipt(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        orderService.printReceipt(orderNo, systemAdmin);
    }

    /**
     * 商户删除订单
     *
     * @param orderNo 订单编号
     * @return Boolean
     */
    @Override
    public Boolean merchantDeleteByOrderNo(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.merchantDeleteByOrderNo(orderNo, systemAdmin);
    }

    /**
     * 商户备注订单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean merchantMark(OrderRemarkRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.merchantMark(request, systemAdmin);
    }

    /**
     * 获取订单快递信息(商户端)
     *
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfoByMerchant(Integer invoiceId) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.getLogisticsInfoByMerchant(invoiceId, systemAdmin);
    }

    /**
     * 核销码核销订单
     *
     * @param verifyCode 核销码
     * @return 核销结果
     */
    @Override
    public Boolean verificationOrderByCode(String verifyCode) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.verificationOrderByCode(verifyCode, systemAdmin, "merchant");
    }

    @Override
    public Boolean verificationOrder(OrderVerificationRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.verificationOrder(request, systemAdmin, "merchant");

    }

    /**
     * 订单细节详情列表
     *
     * @param orderNo 订单号
     * @return 订单细节详情列表
     */
    @Override
    public List<OrderDetailInfoResponse> getDetailList(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        List<OrderDetail> orderDetailList = orderService.getDetailList(orderNo, systemAdmin);
        return orderDetailList.stream().map(orderDetail -> {
            OrderDetailInfoResponse response = new OrderDetailInfoResponse();
            BeanUtils.copyProperties(orderDetail, response);
            response.setSecondType(order.getSecondType());
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_RESERVATION)) {
                if (reservationWorkOrderService.isExistWorkByOrderNo(orderNo)) {
                    response.setIsExistWork(true);
                    List<ReservationWorkOrder> workOrderList = reservationWorkOrderService.findByOrderDetailId(orderDetail.getId());
                    response.setWorkOrderList(workOrderList);
                }
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CARDTIMES)) {
                // 计算退款比例：剩余次数/总次数
                BigDecimal refundRatio = BigDecimal.valueOf(orderDetail.getVerifyRemainingTimes())
                        .divide(BigDecimal.valueOf(orderDetail.getVerifyTotalTimes()), 10, RoundingMode.HALF_UP);
                response.setVerifyRefundPrice(refundRatio.multiply(orderDetail.getPayPrice())
                        .setScale(2, RoundingMode.DOWN));
            }
            return response;
        }).collect(Collectors.toList());

    }

    /**
     * 获取订单发货单列表(商户端)
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> getInvoiceListByMerchant(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.getInvoiceListByMerchant(orderNo, systemAdmin);
    }

    /**
     * 商户直接退款
     */
    @Override
    public Boolean directRefund(MerchantOrderDirectRefundRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.directRefund(request, systemAdmin);
    }

    /**
     * 修改发货单配送信息
     */
    @Override
    public Boolean updateInvoice(OrderInvoiceUpdateRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return orderService.updateInvoice(request, systemAdmin);
    }


}

