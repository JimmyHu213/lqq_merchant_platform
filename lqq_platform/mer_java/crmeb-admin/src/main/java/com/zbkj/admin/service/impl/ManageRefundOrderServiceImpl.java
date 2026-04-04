package com.zbkj.admin.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.ManageRefundOrderService;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * RefundOrderServiceImpl 接口实现
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
public class ManageRefundOrderServiceImpl implements ManageRefundOrderService {


    @Autowired
    private RefundOrderService refundOrderService;
    /**
     * 商户端退款订单分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantRefundOrderPageResponse> getMerchantAdminPage(RefundOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.getMerchantAdminPage(request, systemAdmin);
    }

    /**
     * 获取商户端退款订单各状态数量
     *
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getMerchantOrderStatusNum(RefundOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.getMerchantOrderStatusNum(request, systemAdmin);
    }

    /**
     * 备注退款单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean mark(RefundOrderRemarkRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.mark(request, systemAdmin);
    }

    @Override
    public RefundOrder getInfoException(String refundOrderNo) {
       return refundOrderService.getInfoException(refundOrderNo);
    }

    /**
     * 退款订单列表
     * @param request 搜索参数
     * @return List
     */
    @Override
    public PageInfo<RefundOrderResponse> getH5List(OrderAfterSalesSearchRequest request) {
       return refundOrderService.getH5List(request);
    }

    /**
     * 退款订单详情（移动端）
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    @Override
    public RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(String refundOrderNo) {
        return refundOrderService.getRefundOrderDetailByRefundOrderNo(refundOrderNo);
    }

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.getMerchantDetail(refundOrderNo, systemAdmin);
    }

    /**
     * 平台端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformRefundOrderPageResponse> getPlatformAdminPage(RefundOrderSearchRequest request) {
        return refundOrderService.getPlatformAdminPage(request);
    }

    /**
     * 获取平台端退款订单各状态数量
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getPlatformOrderStatusNum(RefundOrderSearchRequest request) {
        return refundOrderService.getPlatformOrderStatusNum(request);
    }

    /**
     * 平台备注退款单
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean platformMark(RefundOrderRemarkRequest request) {
        return refundOrderService.platformMark(request);
    }

    /**fan
     * 平台端退款订单详情
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getPlatformDetail(String refundOrderNo) {
        return refundOrderService.getPlatformDetail(refundOrderNo);
    }

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    @Override
    public List<RefundOrder> findByDate(Integer merId, String date) {
        return refundOrderService.findByDate(merId, date);
    }

    /**
     * 获取某一月的所有数据
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    @Override
    public List<RefundOrder> findByMonth(Integer merId, String month) {
       return refundOrderService.findByMonth(merId, month);
    }

    /**
     * 根据日期获取退款订单数量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getRefundOrderNumByDate(Integer merId, String date) {
        return refundOrderService.getRefundOrderNumByDate(merId, date);
    }

    /**
     * 根据日期获取退款订单金额
     * @param date 日期
     * @return Integer
     */
    @Override
    public BigDecimal getRefundOrderAmountByDate(Integer merId, String date) {
       return refundOrderService.getRefundOrderAmountByDate(merId, date);
    }

    /**
     * 获取退款中（申请）订单数量
     */
    @Override
    public Integer getRefundingCount(Integer userId) {
        return refundOrderService.getRefundingCount(userId);
    }

    /**
     * 获取退款单详情
     * @param refundOrderNo 退款单号
     */
    @Override
    public RefundOrder getByRefundOrderNo(String refundOrderNo) {
        return refundOrderService.getByRefundOrderNo(refundOrderNo);
    }

    /**
     * 待退款订单数量
     * @return Integer
     */
    @Override
    public Integer getAwaitAuditNum(Integer merId) {
        return refundOrderService.getAwaitAuditNum(merId);
    }

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean revoke(String refundOrderNo) {
        return refundOrderService.revoke(refundOrderNo);
    }

    /**
     * 退款单审核
     * @param request 审核参数
     * @return 审核结果
     */
    @Override
    public Boolean audit(OrderRefundAuditRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.audit(request, systemAdmin);
    }

    /**
     * 退款单收到退货
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean receiving(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.receiving(refundOrderNo, systemAdmin);
    }

    /**
     * 平台强制退款
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean compulsoryRefund(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.compulsoryRefund(refundOrderNo, systemAdmin);
    }

    /**
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     */
    @Override
    public Boolean receivingReject(RejectReceivingRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderService.receivingReject(request, systemAdmin);
    }

    /**
     * 自动撤销退款
     */
    @Override
    public void autoRevoke() {
       refundOrderService.autoRevoke();
    }

    /**
     * 查询支付宝退款定时任务
     */
    @Override
    public Boolean queryAliPayRefund(String refundOrderNo) {
        return refundOrderService.queryAliPayRefund(refundOrderNo);
    }

    /**
     * 订单退款是否包含用户退款
     * @param orderNo 订单编号
     */
    @Override
    public Boolean isOrderContainUserRefund(String orderNo) {
       return refundOrderService.isOrderContainUserRefund(orderNo);
    }

    /**
     * 商户直接退款
     * @param order 订单
     * @param orderDetailList 需要退款的订单详情列表
     */
    @Override
    public Boolean merchantDirectRefund(Order order, List<OrderDetail> orderDetailList, List<Integer> workOrderIdList) {
        return refundOrderService.merchantDirectRefund(order, orderDetailList, workOrderIdList);
    }

    /**
     * 根据第三方单号查询退款单
     * @param outRefundNo 第三方单号
     */
    @Override
    public List<RefundOrder> findByOutRefundNo(String outRefundNo) {
       return refundOrderService.findByOutRefundNo(outRefundNo);
    }

}

