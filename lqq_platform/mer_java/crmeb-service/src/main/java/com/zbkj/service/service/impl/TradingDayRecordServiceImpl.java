package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.record.TradingDayRecord;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.OrderStatisticsChartResponse;
import com.zbkj.service.dao.TradingDayRecordDao;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.RefundOrderService;
import com.zbkj.service.service.TradingDayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ShoppingProductDayRecordService 接口实现
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
public class TradingDayRecordServiceImpl extends ServiceImpl<TradingDayRecordDao, TradingDayRecord> implements TradingDayRecordService {

    @Resource
    private TradingDayRecordDao dao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;

    @Override
    public TradingDayRecord getStatisticsByDateRequest(DateRequest dateRequest) {
        QueryWrapper<TradingDayRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(product_order_num), 0) as product_order_num, " +
                "IFNULL(sum(product_order_pay_num), 0) as product_order_pay_num, " +
                "IFNULL(sum(product_order_pay_fee), 0) as product_order_pay_fee," +
                "IFNULL(sum(product_order_refund_num), 0) as product_order_refund_num, " +
                "IFNULL(sum(product_order_refund_fee), 0) as product_order_refund_fee," +
                "IFNULL(sum(product_order_coupon_fee), 0) as product_order_coupon_fee");
        queryWrapper.between("date", dateRequest.getStartTime(), dateRequest.getEndTime());
        TradingDayRecord record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            record = new TradingDayRecord(0, null, 0, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        if (dateRequest.getIsToday()) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            // 订单数量
            Integer orderNum = orderService.getOrderNumByDate(0, today);
            // 订单支付数量
            Integer payOrderNum = orderService.getPayOrderNumByDate(0, today);
            // 订单支付金额
            BigDecimal payOrderAmount = orderService.getPayOrderAmountByDate(0, today);
            // 订单退款数量
            Integer refundOrderNum = refundOrderService.getRefundOrderNumByDate(0, today);
            // 订单退款金额
            BigDecimal refundOrderAmount = refundOrderService.getRefundOrderAmountByDate(0, today);
            // 订单用劵金额
            BigDecimal couponOrderAmount = orderService.getCouponOrderAmountByDate(0, today);

            record.setProductOrderNum(record.getProductOrderNum() + orderNum);
            record.setProductOrderPayNum(record.getProductOrderPayNum() + payOrderNum);
            record.setProductOrderPayFee(record.getProductOrderPayFee().add(payOrderAmount));
            record.setProductOrderRefundNum(record.getProductOrderRefundNum() + refundOrderNum);
            record.setProductOrderRefundFee(record.getProductOrderRefundFee().add(refundOrderAmount));
            record.setProductOrderCouponFee(record.getProductOrderCouponFee().add(couponOrderAmount));
        }
        return record;
    }

    @Override
    public OrderStatisticsChartResponse getStatisticsByDate(String date, DateRequest dateRequest) {
        LocalDate now = LocalDate.now();
        boolean isToday = Boolean.FALSE;

        QueryWrapper<TradingDayRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(product_order_pay_num), 0) as product_order_pay_num, IFNULL(sum(product_order_pay_fee), 0) as product_order_pay_fee," +
                " IFNULL(sum(product_order_refund_num), 0) as product_order_refund_num, IFNULL(sum(product_order_refund_fee), 0) as product_order_refund_fee");
        if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            queryWrapper.eq("date", date);
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}-\\d{2}$")) {
            queryWrapper.like("date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                queryWrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                queryWrapper.le("date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_MONTH));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}$")) {
            queryWrapper.like("date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                queryWrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                queryWrapper.le("date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_YEAR));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        }
        TradingDayRecord record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            record = new TradingDayRecord(0, null, 0, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        Integer payOrderNum = 0;
        BigDecimal payOrderAmount = BigDecimal.ZERO;
        Integer refundOrderNum = 0;
        BigDecimal refundOrderAmount = BigDecimal.ZERO;
        if (isToday) {
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            // 订单支付数量
            payOrderNum = orderService.getPayOrderNumByDate(0, today);
            // 订单支付金额
            payOrderAmount = orderService.getPayOrderAmountByDate(0, today);
            // 订单退款数量
            refundOrderNum = refundOrderService.getRefundOrderNumByDate(0, today);
            // 订单退款金额
            refundOrderAmount = refundOrderService.getRefundOrderAmountByDate(0, today);
        }
        OrderStatisticsChartResponse response = new OrderStatisticsChartResponse();
        response.setDate(date);
        response.setOrderPrice(record.getProductOrderPayFee().add(payOrderAmount));
        response.setOrderVolume(record.getProductOrderPayNum() + payOrderNum);
        response.setRefundPrice(record.getProductOrderRefundFee().add(refundOrderAmount));
        response.setRefundVolume(record.getProductOrderRefundNum() + refundOrderNum);
        return response;
    }
}

