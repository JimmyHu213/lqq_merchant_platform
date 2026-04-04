package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.bill.MerchantDailyStatement;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.record.TradingDayRecord;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.OrderStatisticsChartResponse;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理 service 调用业务
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
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private TradingDayRecordService tradingDayRecordService;
    @Autowired
    private MerchantDailyStatementService merchantDailyStatementService;


    /**
     * 平台端-订单顶部核心数据统计
     * @param dateRequest 日期请求对象
     */
    @Override
    public List<StatisticsTopResponse> topPlatformData(DateRequest dateRequest) {
        // 获取上个时间段的数据
        DateRequest lastDateRequest = getLastDateRequest(dateRequest.getStartTime(), dateRequest.getEndTime());

        // 判断今天是否在时间段内
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
        if (today.compareTo(dateRequest.getStartTime()) >= 0 && today.compareTo(dateRequest.getEndTime()) <= 0) {
            dateRequest.setIsToday(Boolean.TRUE);
        }
        if (today.compareTo(lastDateRequest.getStartTime()) >= 0 && today.compareTo(lastDateRequest.getEndTime()) <= 0) {
            lastDateRequest.setIsToday(Boolean.TRUE);
        }
        // 初始化数据
        List<StatisticsTopResponse> responses = initTopData();

        TradingDayRecord record = tradingDayRecordService.getStatisticsByDateRequest(dateRequest);
        TradingDayRecord lastRecord = tradingDayRecordService.getStatisticsByDateRequest(lastDateRequest);
        // 下单数
        calculateGrowth(responses, 0, record.getProductOrderNum(), lastRecord.getProductOrderNum());
        // 支付订单数
        calculateGrowth(responses, 1, record.getProductOrderPayNum(), lastRecord.getProductOrderPayNum());
        // 退款订单数
        calculateGrowth(responses, 2, record.getProductOrderRefundNum(), lastRecord.getProductOrderRefundNum());
        // 订单实付金额
        calculateGrowth(responses, 3, record.getProductOrderPayFee(), lastRecord.getProductOrderPayFee());
        // 用券金额
        calculateGrowth(responses, 4, record.getProductOrderCouponFee(), lastRecord.getProductOrderCouponFee());
        // 退款金额
        calculateGrowth(responses, 5, record.getProductOrderRefundFee(), lastRecord.getProductOrderRefundFee());
        return responses;
    }

    /**
     * 平台端-订单折线趋势图统计
     * @param dateRequest 日期请求对象
     */
    @Override
    public List<OrderStatisticsChartResponse> chartPlatformData(DateRequest dateRequest) {
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<OrderStatisticsChartResponse> responses = new ArrayList<>();

        dateList.forEach(date->{
            OrderStatisticsChartResponse response= tradingDayRecordService.getStatisticsByDate(date, dateRequest);
            responses.add(response);
        });

        return responses;
    }

    /**
     * 平台端-订单类型统计
     */
    @Override
    public List<StatisticsPieResponse> typePlatformData() {
        ArrayList<StatisticsPieResponse> responses = new ArrayList<>();
        List<Order> orderList = orderService.statistics(0);
        if (ObjectUtils.isNull(orderList)) {
            return new ArrayList<>();
        }
        Map<Integer, Long> typeCountMap = orderList.stream()
                .filter(order -> ObjectUtils.isNotNull(order.getSecondType()))
                .collect(Collectors.groupingBy(
                        Order::getSecondType,
                        Collectors.counting()
                ));
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(0, "普通订单");
        typeMap.put(1, "积分订单");
        typeMap.put(2, "虚拟订单");
        typeMap.put(4, "视频号订单");
        typeMap.put(5, "云盘订单");
        typeMap.put(6, "卡密订单");
        typeMap.put(7, "预约订单");
        typeMap.put(8, "次卡订单");
        typeCountMap.forEach((key, value) -> {
            StatisticsPieResponse response = new StatisticsPieResponse();
            response.setName(typeMap.get(key));
            response.setValue(Integer.valueOf(value.toString()));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 平台端-发货方式统计
     */
    @Override
    public List<StatisticsPieResponse> shippingTypePlatformData() {
        ArrayList<StatisticsPieResponse> responses = new ArrayList<>();
        List<MerchantOrder> merchantOrderList = merchantOrderService.statistics(0);
        if (ObjectUtils.isNull(merchantOrderList)) {
            return new ArrayList<>();
        }
        Map<Integer, Long> shippingCountMap = merchantOrderList.stream()
                .filter(merchantOrder -> ObjectUtils.isNotNull(merchantOrder.getShippingType()))
                .collect(Collectors.groupingBy(
                        MerchantOrder::getShippingType,
                        Collectors.counting()
                ));
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(1, "快递");
        typeMap.put(2, "门店自提");
        typeMap.put(3, "虚拟发货");
        typeMap.put(4, "到店服务");
        typeMap.put(5, "上门服务");
        shippingCountMap.forEach((key, value) -> {
            StatisticsPieResponse response = new StatisticsPieResponse();
            response.setName(typeMap.get(key));
            response.setValue(Integer.valueOf(value.toString()));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 商户端-订单顶部核心数据统计
     * @param dateRequest 日期请求对象
     */
    @Override
    public List<StatisticsTopResponse> topMerchantData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        // 获取上个时间段的数据
        DateRequest lastDateRequest = getLastDateRequest(dateRequest.getStartTime(), dateRequest.getEndTime());

        // 判断今天是否在时间段内
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
        if (today.compareTo(dateRequest.getStartTime()) >= 0 && today.compareTo(dateRequest.getEndTime()) <= 0) {
            dateRequest.setIsToday(Boolean.TRUE);
        }
        if (today.compareTo(lastDateRequest.getStartTime()) >= 0 && today.compareTo(lastDateRequest.getEndTime()) <= 0) {
            lastDateRequest.setIsToday(Boolean.TRUE);
        }
        // 初始化数据
        List<StatisticsTopResponse> responses = initTopData();

        MerchantDailyStatement record = merchantDailyStatementService.getStatisticsByDateRequest(dateRequest, merId);
        MerchantDailyStatement lastRecord = merchantDailyStatementService.getStatisticsByDateRequest(lastDateRequest, merId);
        // 下单数
        calculateGrowth(responses, 0, orderService.getOrderNumByMerIdAndDateRequest(dateRequest, merId),
                orderService.getOrderNumByMerIdAndDateRequest(lastDateRequest, merId));
        // 支付订单数
        calculateGrowth(responses, 1, record.getOrderNum(), lastRecord.getOrderNum());
        // 退款订单数
        calculateGrowth(responses, 2, record.getRefundNum(), lastRecord.getRefundNum());
        // 订单实付金额
        calculateGrowth(responses, 3, record.getOrderPayAmount(), lastRecord.getOrderPayAmount());
        // 用券金额
        calculateGrowth(responses, 4, record.getPlatCouponPrice(), lastRecord.getPlatCouponPrice());
        // 退款金额
        calculateGrowth(responses, 5, record.getOrderRefundPrice(), lastRecord.getOrderRefundPrice());
        return responses;
    }

    /**
     * 商户端-订单折线趋势图统计
     * @param dateRequest 日期请求对象
     */
    @Override
    public List<OrderStatisticsChartResponse> chartMerchantData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<OrderStatisticsChartResponse> responses = new ArrayList<>();

        dateList.forEach(date->{
            OrderStatisticsChartResponse response = merchantDailyStatementService.getStatisticsByDate(date, dateRequest, merId);
            responses.add(response);
        });

        return responses;
    }

    /**
     * 商户端-订单类型统计
     */
    @Override
    public List<StatisticsPieResponse> typeMerchantData() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        ArrayList<StatisticsPieResponse> responses = new ArrayList<>();
        List<Order> orderList = orderService.statistics(merId);
        if (ObjectUtils.isNull(orderList)) {
            return new ArrayList<>();
        }
        Map<Integer, Long> typeCountMap = orderList.stream()
                .filter(order -> ObjectUtils.isNotNull(order.getSecondType()))
                .collect(Collectors.groupingBy(
                        Order::getSecondType,
                        Collectors.counting()
                ));
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(0, "普通订单");
        typeMap.put(1, "积分订单");
        typeMap.put(2, "虚拟订单");
        typeMap.put(4, "视频号订单");
        typeMap.put(5, "云盘订单");
        typeMap.put(6, "卡密订单");
        typeMap.put(7, "预约订单");
        typeMap.put(8, "次卡订单");
        typeCountMap.forEach((key, value) -> {
            StatisticsPieResponse response = new StatisticsPieResponse();
            response.setName(typeMap.get(key));
            response.setValue(Integer.valueOf(value.toString()));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 商户端-发货方式统计
     */
    @Override
    public List<StatisticsPieResponse> shippingTypeMerchantData() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        ArrayList<StatisticsPieResponse> responses = new ArrayList<>();
        List<MerchantOrder> merchantOrderList = merchantOrderService.statistics(merId);
        if (ObjectUtils.isNull(merchantOrderList)) {
            return new ArrayList<>();
        }
        Map<Integer, Long> shippingCountMap = merchantOrderList.stream()
                .filter(merchantOrder -> ObjectUtils.isNotNull(merchantOrder.getShippingType()))
                .collect(Collectors.groupingBy(
                        MerchantOrder::getShippingType,
                        Collectors.counting()
                ));
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(1, "快递");
        typeMap.put(2, "门店自提");
        typeMap.put(3, "虚拟发货");
        typeMap.put(4, "到店服务");
        typeMap.put(5, "上门服务");
        shippingCountMap.forEach((key, value) -> {
            StatisticsPieResponse response = new StatisticsPieResponse();
            response.setName(typeMap.get(key));
            response.setValue(Integer.valueOf(value.toString()));
            responses.add(response);
        });
        return responses;
    }


    /**
     * 初始化顶部核心数据
     * @return 顶部核心数据封装对象集合
     */
    private List<StatisticsTopResponse> initTopData() {
        List<StatisticsTopResponse> responses = new ArrayList<>();
        StatisticsTopResponse res1= new StatisticsTopResponse("下单数",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res2= new StatisticsTopResponse("支付订单数",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res3= new StatisticsTopResponse("退款订单数",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res4= new StatisticsTopResponse("订单实付金额", BigDecimal.ZERO,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res5= new StatisticsTopResponse("用券金额", BigDecimal.ZERO,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res6= new StatisticsTopResponse("退款金额", BigDecimal.ZERO,"0.00%", Boolean.FALSE);
        Collections.addAll(responses,res1,res2,res3,res4,res5,res6);
        return responses;
    }

    /**
     * 获取上一个时间段的数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 上一个时间段的数据封装对象 DateRequest 类型
     */
    private DateRequest getLastDateRequest(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE);
        LocalDate start = LocalDate.parse(startTime, formatter);
        LocalDate end = LocalDate.parse(endTime, formatter);

        long periodDays = ChronoUnit.DAYS.between(start, end) + 1;
        LocalDate previousStart = start.minusDays(periodDays);
        LocalDate previousEnd = end.minusDays(periodDays);

        String lastStart = previousStart.format(formatter);
        String lastEnd = previousEnd.format(formatter);
        // 封装上一个时间段对象
        DateRequest dateRequest = new DateRequest();
        dateRequest.setStartTime(lastStart);
        dateRequest.setEndTime(lastEnd);
        return dateRequest;
    }

    /**
     * 计算增长率
     * @param responses 响应数据集合
     * @param index 索引位置
     * @param currentValue 当前值 Integer 类型
     * @param lastValue 上一个时间段的值 Integer 类型
     */
    private void calculateGrowth(List<StatisticsTopResponse> responses, Integer index, Integer currentValue, Integer lastValue) {
        String growthRate = "0.00%";
        Boolean increased = Boolean.FALSE;
        if (lastValue > 0) {
            BigDecimal growth = new BigDecimal(currentValue).subtract(new BigDecimal(lastValue))
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(lastValue), 2, RoundingMode.HALF_UP);
            growthRate = growth.toString() + "%";
            if (growth.compareTo(BigDecimal.ZERO) > 0) {
                increased = Boolean.TRUE;
            }
        } else if (currentValue > 0) {
            growthRate = "100.00%";
            increased = Boolean.TRUE;
        }
        responses.get(index).setValue(currentValue);
        responses.get(index).setGrowthRate(growthRate);
        responses.get(index).setIncreased(increased);
    }

    /**
     * 计算增长率
     * @param responses 响应数据集合
     * @param index 索引位置
     * @param currentValue 当前值 BigDecimal 类型
     * @param lastValue 上一个时间段的值 BigDecimal 类型
     */
    private void calculateGrowth(List<StatisticsTopResponse> responses, Integer index, BigDecimal currentValue, BigDecimal lastValue) {
        String growthRate = "0.00%";
        Boolean increased = Boolean.FALSE;
        if (lastValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal growth = currentValue.subtract(lastValue)
                    .multiply(new BigDecimal("100"))
                    .divide(lastValue, 2, RoundingMode.HALF_UP);
            growthRate = growth.toString() + "%";
            if (growth.compareTo(BigDecimal.ZERO) > 0) {
                increased = Boolean.TRUE;
            }
        } else if (currentValue.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = "100.00%";
            increased = Boolean.TRUE;
        }
        responses.get(index).setValue(currentValue);
        responses.get(index).setGrowthRate(growthRate);
        responses.get(index).setIncreased(increased);
    }
}
