package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.bill.MerchantDailyStatement;
import com.zbkj.common.model.order.OrderProfitSharing;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.statistcs.OrderStatisticsChartResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.MerchantDailyStatementDao;
import com.zbkj.service.service.MerchantDailyStatementService;
import com.zbkj.service.service.OrderProfitSharingService;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MerchantDailyStatementServiceImpl 接口实现
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
public class MerchantDailyStatementServiceImpl extends ServiceImpl<MerchantDailyStatementDao, MerchantDailyStatement> implements MerchantDailyStatementService {

    @Resource
    private MerchantDailyStatementDao dao;

    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;

    /**
     * 查询某一天的所有商户日帐单
     *
     * @param date 日期:年-月-日
     * @return List
     */
    @Override
    public List<MerchantDailyStatement> findByDate(String date) {
        LambdaQueryWrapper<MerchantDailyStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDailyStatement::getDataDate, date);
        return dao.selectList(lqw);
    }

    /**
     * 获取某个月的所有帐单
     *
     * @param month 月份：年-月
     * @return List
     */
    @Override
    public List<MerchantDailyStatement> findByMonth(String month) {
        LambdaQueryWrapper<MerchantDailyStatement> lqw = Wrappers.lambdaQuery();
        lqw.apply("date_format(data_date, '%Y-%m') = {0}", month);
        return dao.selectList(lqw);
    }

    /**
     * 分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantDailyStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<MerchantDailyStatement> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantDailyStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDailyStatement::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(dateLimit)) {
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getDateLimit(dateLimit);
            String startDate = DateUtil.parse(dateLimitVo.getStartTime()).toDateStr();
            String endDate = DateUtil.parse(dateLimitVo.getEndTime()).toDateStr();
            lqw.between(MerchantDailyStatement::getDataDate, startDate, endDate);
        }
        lqw.orderByDesc(MerchantDailyStatement::getId);
        List<MerchantDailyStatement> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        // 判断是否包含今天
        String today = DateUtil.date().toDateStr();
        for (MerchantDailyStatement dailyStatement : list) {
            if (!dailyStatement.getDataDate().equals(today)) {
                continue;
            }
            writeDailyStatement(dailyStatement);
        }
        return CommonPage.copyPageInfo(page, list);
    }


    @Override
    public MerchantDailyStatement getStatisticsByDateRequest(DateRequest dateRequest, Integer merId) {
        QueryWrapper<MerchantDailyStatement> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(order_num), 0) as order_num, IFNULL(sum(order_pay_amount), 0) as order_pay_amount," +
                "IFNULL(sum(refund_num), 0) as refund_num, IFNULL(sum(order_refund_price), 0) as order_refund_price," +
                "IFNULL(sum(plat_coupon_price), 0) as plat_coupon_price");
        queryWrapper.eq("mer_id", merId);
        queryWrapper.between("data_date", dateRequest.getStartTime(), dateRequest.getEndTime());
        MerchantDailyStatement record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            record = new MerchantDailyStatement(0, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    0, BigDecimal.ZERO, 0, BigDecimal.ZERO, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        if (dateRequest.getIsToday()) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            // 订单支付数量
            Integer payOrderNum = orderService.getPayOrderNumByDate(merId, today);
            // 订单支付金额
            BigDecimal payOrderAmount = orderService.getPayOrderAmountByDate(merId, today);
            // 订单退款数量
            Integer refundOrderNum = refundOrderService.getRefundOrderNumByDate(merId, today);
            // 订单退款金额
            BigDecimal refundOrderAmount = refundOrderService.getRefundOrderAmountByDate(merId, today);
            // 订单用劵金额
            BigDecimal couponOrderAmount = orderService.getCouponOrderAmountByDate(merId, today);

            record.setOrderNum(record.getOrderNum() + payOrderNum);
            record.setOrderPayAmount(record.getOrderPayAmount().add(payOrderAmount));
            record.setRefundNum(record.getRefundNum() + refundOrderNum);
            record.setOrderRefundPrice(record.getOrderRefundPrice().add(refundOrderAmount));
            record.setPlatCouponPrice(record.getPlatCouponPrice().add(couponOrderAmount));
        }
        return record;
    }

    @Override
    public OrderStatisticsChartResponse getStatisticsByDate(String date, DateRequest dateRequest, Integer merId) {
        LocalDate now = LocalDate.now();
        boolean isToday = Boolean.FALSE;

        QueryWrapper<MerchantDailyStatement> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(order_num), 0) as order_num, IFNULL(sum(order_pay_amount), 0) as order_pay_amount," +
                "IFNULL(sum(refund_num), 0) as refund_num, IFNULL(sum(order_refund_price), 0) as order_refund_price");
        queryWrapper.eq("mer_id", merId);
        if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            queryWrapper.eq("data_date", date);
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}-\\d{2}$")) {
            queryWrapper.like("data_date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                queryWrapper.ge("data_date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                queryWrapper.le("data_date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_MONTH));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}$")) {
            queryWrapper.like("data_date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                queryWrapper.ge("data_date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                queryWrapper.le("data_date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_YEAR));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        }
        MerchantDailyStatement record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            record = new MerchantDailyStatement(0, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    0, BigDecimal.ZERO, 0, BigDecimal.ZERO, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        Integer payOrderNum = 0;
        BigDecimal payOrderAmount = BigDecimal.ZERO;
        Integer refundOrderNum = 0;
        BigDecimal refundOrderAmount = BigDecimal.ZERO;
        if (isToday) {
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            // 订单支付数量
            payOrderNum = orderService.getPayOrderNumByDate(merId, today);
            // 订单支付金额
            payOrderAmount = orderService.getPayOrderAmountByDate(merId, today);
            // 订单退款数量
            refundOrderNum = refundOrderService.getRefundOrderNumByDate(merId, today);
            // 订单退款金额
            refundOrderAmount = refundOrderService.getRefundOrderAmountByDate(merId, today);
        }
        OrderStatisticsChartResponse response = new OrderStatisticsChartResponse();
        response.setDate(date);
        response.setOrderPrice(record.getOrderPayAmount().add(payOrderAmount));
        response.setOrderVolume(record.getOrderNum() + payOrderNum);
        response.setRefundPrice(record.getOrderRefundPrice().add(refundOrderAmount));
        response.setRefundVolume(record.getRefundNum() + refundOrderNum);
        return response;
    }

    /**
     * 为帐单写入数据
     *
     * @param statement 帐单
     */
    private void writeDailyStatement(MerchantDailyStatement statement) {
        List<OrderProfitSharing> sharingList = orderProfitSharingService.findByDate(statement.getMerId(), statement.getDataDate());
        List<RefundOrder> refundOrderList = refundOrderService.findByDate(statement.getMerId(), statement.getDataDate());

        // 订单支付总金额
        BigDecimal orderPayAmount = new BigDecimal("0.00");
        // 订单支付笔数
        int orderNum = 0;
        // 商户分账金额
        BigDecimal orderIncomeAmount = new BigDecimal("0.00");
        // 平台手续费
        BigDecimal handlingFee = new BigDecimal("0.00");
        // 一二级分佣金额
        BigDecimal firstBrokerage = new BigDecimal("0.00");
        BigDecimal secondBrokerage = new BigDecimal("0.00");
        // 商户退款金额
        BigDecimal refundAmount = new BigDecimal("0.00");
        // 退款笔数
        int refundNum = 0;

        BigDecimal platCouponPrice = new BigDecimal("0.00");
        BigDecimal integralPrice = new BigDecimal("0.00");
        BigDecimal brokeragePrice = new BigDecimal("0.00");

        // 退款
        BigDecimal orderRefundPrice = new BigDecimal("0.00");
        BigDecimal refundPlatCouponPrice = new BigDecimal("0.00");
        BigDecimal refundIntegralPrice = new BigDecimal("0.00");
        BigDecimal refundHandlingFee = new BigDecimal("0.00");
        BigDecimal refundBrokeragePrice = new BigDecimal("0.00");
        BigDecimal refundMerchantTransferAmount = new BigDecimal("0.00");

        BigDecimal freightFee = new BigDecimal("0.00");
        BigDecimal refundFreightFee = new BigDecimal("0.00");

        if (CollUtil.isNotEmpty(sharingList)) {
            orderPayAmount =  sharingList.stream().map(OrderProfitSharing::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderNum = sharingList.size();
            orderIncomeAmount = sharingList.stream().map(OrderProfitSharing::getProfitSharingMerPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            handlingFee = sharingList.stream().map(OrderProfitSharing::getProfitSharingPlatPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            firstBrokerage = sharingList.stream().map(OrderProfitSharing::getFirstBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            secondBrokerage = sharingList.stream().map(OrderProfitSharing::getSecondBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            platCouponPrice = sharingList.stream().map(OrderProfitSharing::getPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            integralPrice = sharingList.stream().map(OrderProfitSharing::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            brokeragePrice = firstBrokerage.add(secondBrokerage);
            freightFee = sharingList.stream().map(OrderProfitSharing::getFreightFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (CollUtil.isNotEmpty(refundOrderList)) {
            refundAmount = refundOrderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundNum = refundOrderList.size();
            refundMerchantTransferAmount = refundOrderList.stream().map(RefundOrder::getMerchantRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundHandlingFee = refundOrderList.stream().map(RefundOrder::getPlatformRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundPlatCouponPrice = refundOrderList.stream().map(RefundOrder::getRefundPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundBrokeragePrice = refundOrderList.stream().map(e -> e.getRefundFirstBrokerageFee().add(e.getRefundSecondBrokerageFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundIntegralPrice = refundOrderList.stream().map(RefundOrder::getRefundIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderRefundPrice = refundOrderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundFreightFee = refundOrderList.stream().map(RefundOrder::getRefundFreightFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

//        // 支出总金额 = 商户退款金额
//        BigDecimal payoutAmount = refundAmount;
//        // 支出笔数 = 退款笔数
//        int payoutNum = refundNum;
//        // 商户日收支 = 订单收入金额 - 商户退款金额
//        BigDecimal incomeExpenditure = orderIncomeAmount.subtract(refundAmount);

        // 订单应收 = 订单实付 + 平优 + 平积
        BigDecimal orderReceivable = orderPayAmount.add(platCouponPrice).add(integralPrice);

        // 订单应退 = 商户退款 + 平台退款 + 佣金退款
        BigDecimal orderRefundable = BigDecimal.ZERO.subtract(refundMerchantTransferAmount).subtract(refundHandlingFee).subtract(refundBrokeragePrice);

        // 订单实退 = 订单应退 - 退平台优惠券 - 退平台积分
        BigDecimal orderRefund = orderRefundable.subtract(refundPlatCouponPrice).subtract(refundIntegralPrice);

        // 平台手续费
        BigDecimal platformHandling = handlingFee.subtract(refundHandlingFee);

        // 佣金
        BigDecimal brokerage = brokeragePrice.subtract(refundBrokeragePrice);

        // 实际收入 = 订单应收 - 订单应退
        BigDecimal income = orderReceivable.add(orderRefundable);

        // 实际支出 = 平台手续费 + 佣金
        BigDecimal payout = platformHandling.add(brokerage);

        // 当日结余
        BigDecimal incomeExpenditure = income.subtract(payout);

        statement.setOrderPayAmount(orderPayAmount);
        statement.setOrderNum(orderNum);
        statement.setOrderIncomeAmount(orderIncomeAmount);
        statement.setHandlingFee(handlingFee);
        statement.setFirstBrokerage(firstBrokerage);
        statement.setSecondBrokerage(secondBrokerage);
//        statement.setPayoutAmount(payoutAmount);
//        statement.setPayoutNum(payoutNum);
        statement.setRefundAmount(refundAmount);
        statement.setRefundNum(refundNum);
        statement.setIncomeExpenditure(incomeExpenditure.abs());

        statement.setPlatCouponPrice(platCouponPrice);
        statement.setIntegralPrice(integralPrice);
        statement.setBrokeragePrice(brokeragePrice);
        statement.setOrderRefundPrice(orderRefundPrice);
        statement.setRefundPlatCouponPrice(refundPlatCouponPrice);
        statement.setRefundIntegralPrice(refundIntegralPrice);
        statement.setRefundHandlingFee(refundHandlingFee);
        statement.setRefundBrokeragePrice(refundBrokeragePrice);
        statement.setRefundMerchantTransferAmount(refundMerchantTransferAmount);

        statement.setFreightFee(freightFee);
        statement.setRefundFreightFee(refundFreightFee);
    }
}

