package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.request.ShoppingCreditsRefundOrderSearchRequest;
import com.zbkj.admin.response.ShoppingCreditsRefundOrderInfoResponse;
import com.zbkj.admin.service.ManageShoppingCreditsRefundOrderService;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.OrderRefundAuditRequest;
import com.zbkj.common.response.ShoppingCreditsRefundOrderPageResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/5
 */
@Service
public class ManageShoppingCreditsRefundOrderServiceImpl implements ManageShoppingCreditsRefundOrderService {

    @Autowired
    private ShoppingCreditsRefundOrderService shoppingCreditsRefundOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private ShoppingCreditsOrderService shoppingCreditsOrderService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private WechatPayService wechatPayService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private UserShoppingCreditsService userShoppingCreditsService;
    @Autowired
    private UserShoppingCreditsRecordService userShoppingCreditsRecordService;


    /**
     * 购物金订单分页列表
     */
    @Override
    public PageInfo<ShoppingCreditsRefundOrderPageResponse> findPage(ShoppingCreditsRefundOrderSearchRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();

        map.put("merId", admin.getMerId());
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (StrUtil.isNotBlank(request.getRefundOrderNo())) {
            map.put("refundOrderNo", URLUtil.decode(request.getRefundOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getRefundStatus()) && request.getRefundStatus() < 99) {
            map.put("refundStatus", request.getRefundStatus());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(request.getDateLimit());
            if (StrUtil.isNotBlank(dateLimitUtilVo.getStartTime()) && StrUtil.isNotBlank(dateLimitUtilVo.getEndTime())) {
                //判断时间
                int compareDateResult = CrmebDateUtil.compareDate(dateLimitUtilVo.getEndTime(), dateLimitUtilVo.getStartTime(), DateConstants.DATE_FORMAT);
                if (compareDateResult == -1) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
                }
                map.put("startTime", dateLimitUtilVo.getStartTime());
                map.put("endTime", dateLimitUtilVo.getEndTime());
            }
        }
        if (StrUtil.isNotBlank(request.getSearchType()) && StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        Page<ShoppingCreditsRefundOrderPageResponse> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ShoppingCreditsRefundOrderPageResponse> responseList = shoppingCreditsRefundOrderService.findPageByMerchant(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 退款单详情-商户端
     *
     * @param refundOrderNo 退款单号
     */
    @Override
    public ShoppingCreditsRefundOrderInfoResponse getInfoByMerchant(String refundOrderNo) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        ShoppingCreditsRefundOrder refundOrder = shoppingCreditsRefundOrderService.getByRefundOrderNo(URLUtil.decode(refundOrderNo));
        if (ObjectUtil.isNull(refundOrder) || !refundOrder.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "退款订单不存在");
        }
        User user = userService.getById(refundOrder.getUid());

        ShoppingCreditsRefundOrderInfoResponse response = new ShoppingCreditsRefundOrderInfoResponse();
        response.setUid(refundOrder.getUid());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setRefundOrderNo(refundOrder.getRefundOrderNo());
        response.setOrderNo(refundOrder.getOrderNo());
        response.setRefundAmount(refundOrder.getRefundAmount());
        response.setRefundGiftAmount(refundOrder.getRefundGiftAmount());
        response.setRefundReason(refundOrder.getRefundReason());
        response.setCreateTime(refundOrder.getCreateTime());
        response.setRefundStatus(refundOrder.getRefundStatus());
        response.setRefusingRefundReason(refundOrder.getRefusingRefundReason());
        response.setMerRemark(refundOrder.getMerRemark());
        if (refundOrder.getRefundStatus() > 0 && refundOrder.getRefundStatus() < 4) {
            if (refundOrder.getAuditType().equals(2)) {
                SystemAdmin audit = systemAdminService.getById(refundOrder.getAuditId());
                response.setAuditId(audit.getId());
                response.setAuditName(audit.getRealName());
            }
            if (refundOrder.getAuditType().equals(3)) {
                User auditUser = userService.getById(refundOrder.getAuditId());
                response.setAuditId(auditUser.getId());
                response.setAuditName(auditUser.getNickname());
            }
            response.setAuditTime(refundOrder.getAuditTime());
        }
        return response;
    }

    @Override
    public Boolean audit(OrderRefundAuditRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        ShoppingCreditsRefundOrder refundOrder = shoppingCreditsRefundOrderService.getByRefundOrderNo(request.getRefundOrderNo());
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        ShoppingCreditsOrder order = shoppingCreditsOrderService.getByOrderNo(refundOrder.getOrderNo());
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST.setMessage("退款单关联的订单不存在"));
        }
        refundOrder.setAuditId(systemAdmin.getId());
        refundOrder.setAuditType(2);
        refundOrder.setAuditTime(DateUtil.date());
        // 审核失败
        if (request.getAuditType().equals(RefundOrderConstants.REFUND_ORDER_AUDIT_REFUSE)) {
            return auditRefuseProcess(order, refundOrder, request.getReason());
        }
        // 审核成功
        // 仅退款
        Boolean refundResult = refundPrice(refundOrder, order);
        return refundResult;
    }

    /**
     * 审核失败处理
     */
    private Boolean auditRefuseProcess(ShoppingCreditsOrder order, ShoppingCreditsRefundOrder refundOrder, String reason) {

        Boolean execute = transactionTemplate.execute(e -> {
            shoppingCreditsRefundOrderService.auditRefuse(refundOrder.getRefundOrderNo(), reason, refundOrder.getAuditId(), refundOrder.getAuditType());
            shoppingCreditsOrderService.updateRefundStatus(order.getOrderNo(), 0);
            // 拒绝退款，需要回滚用户购物金
            userShoppingCreditsService.updateAmount(order.getUid(), order.getMerId(), refundOrder.getRefundAmount(), refundOrder.getRefundGiftAmount(), Constants.OPERATION_TYPE_ADD);
            userShoppingCreditsRecordService.deleteByLinkNo(refundOrder.getRefundOrderNo());
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 退款
     *
     * @param refundOrder 退款单
     */
    private Boolean refundPrice(ShoppingCreditsRefundOrder refundOrder, ShoppingCreditsOrder order) {
        BigDecimal refundPrice = refundOrder.getRefundAmount();
        refundOrder.setRefundPayType(order.getPayType());

        //退款
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                wxRefund(order, refundOrder.getRefundOrderNo(), refundPrice, order.getRechargeAmount());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("微信申请退款失败！" + e.getMessage());
            }
        }
        Boolean aliPayIsRefund = false;
        if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                aliPayIsRefund = aliPayService.refund(order.getOrderNo(), refundOrder.getRefundOrderNo(), refundOrder.getRefundReason(), refundPrice);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("支付宝申请退款失败！" + e.getMessage());
            }
        }

        refundOrder.setRefundTime(DateUtil.date());
        refundOrder.setUpdateTime(DateUtil.date());
        Boolean finalAliPayIsRefund = aliPayIsRefund;
        Boolean execute = transactionTemplate.execute(e -> {
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING);
            if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && finalAliPayIsRefund) {
                refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
            }
            shoppingCreditsRefundOrderService.updateById(refundOrder);
            return Boolean.TRUE;
        });
        if (execute) {
            order.setRefundStatus(2);
            order.setUpdateTime(DateUtil.date());
            shoppingCreditsOrderService.updateById(order);
        }
        return execute;
    }

    /**
     * 微信退款
     *
     * @param order         订单
     * @param refundOrderNo 退款单号
     * @param refundPrice   退款金额
     * @param totalPrice    订单支付总金额
     */
    private void wxRefund(ShoppingCreditsOrder order, String refundOrderNo, BigDecimal refundPrice, BigDecimal totalPrice) {
        String paySource = order.getPayChannel();
        if (paySource.equals(PayConstants.PAY_CHANNEL_H5) || paySource.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
            String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
            if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                paySource = PayConstants.PAY_CHANNEL_WECHAT_MINI;
            } else {
                paySource = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
            }
        }
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        if ("V2".equals(crmebConfig.getWxPayVersion())) {
            // 组装请求退款对象
            WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
            wxPayRefundRequest.setOutTradeNo(order.getOutTradeNo());
            wxPayRefundRequest.setOutRefundNo(refundOrderNo);
            wxPayRefundRequest.setTotalFee(totalPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
            wxPayRefundRequest.setRefundFee(refundPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
            wechatPayService.refund(wxPayRefundRequest, paySource, apiDomain);
        } else {
            WxPayRefundV3Request wxPayRefundV3Request = new WxPayRefundV3Request();
            wxPayRefundV3Request.setOutTradeNo(order.getOutTradeNo());
            wxPayRefundV3Request.setOutRefundNo(refundOrderNo);
            WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
            amount.setTotal(totalPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
            amount.setCurrency("CNY");
            amount.setRefund(refundPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
            wxPayRefundV3Request.setAmount(amount);
            wechatPayService.refundV3(wxPayRefundV3Request, paySource, apiDomain);
        }
    }
}
