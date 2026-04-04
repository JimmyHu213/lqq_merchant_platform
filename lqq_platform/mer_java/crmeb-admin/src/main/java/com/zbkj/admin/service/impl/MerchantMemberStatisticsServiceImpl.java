package com.zbkj.admin.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.zbkj.admin.request.StatisticsDateRequest;
import com.zbkj.admin.response.MerchantMemberConsumeTrendDataResponse;
import com.zbkj.admin.response.MerchantMemberMomDataResponse;
import com.zbkj.admin.response.MerchantMemberNewTrendDataResponse;
import com.zbkj.admin.response.MerchantMemberOverviewResponse;
import com.zbkj.admin.service.MerchantMemberStatisticsService;
import com.zbkj.common.dto.LevelMemberNumDto;
import com.zbkj.common.dto.MemberRegisterNumDto;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商户会员统计服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Service
public class MerchantMemberStatisticsServiceImpl implements MerchantMemberStatisticsService {

    @Autowired
    private MerchantMemberService merchantMemberService;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantMemberLevelService merchantMemberLevelService;
    @Autowired
    private ShoppingCreditsOrderService shoppingCreditsOrderService;
    @Autowired
    private OrderService orderService;


    /**
     * 会员概览
     */
    @Override
    public MerchantMemberOverviewResponse overviewData() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantMemberOverviewResponse response = new MerchantMemberOverviewResponse();
        Integer memberNum = merchantMemberService.getCountByMerId(admin.getMerId());
        response.setMemberNum(memberNum);

        List<LevelMemberNumDto> levelMemberNumDtoList = merchantMemberLevelService.findMerchantNumListByMerId(admin.getMerId());
        HashMap<String, Integer> levelMap = new HashMap<>();
        for (LevelMemberNumDto numDto : levelMemberNumDtoList) {
            levelMap.put(numDto.getName(), numDto.getNum());
        }
        response.setMemberLevelMap(levelMap);

        List<MemberRegisterNumDto> registerNumDtoList = merchantMemberService.findRegisterTypeNumListByMerId(admin.getMerId());
        HashMap<String, Integer> sourceMap = new HashMap<>();
        for (MemberRegisterNumDto numDto : registerNumDtoList) {
            switch (numDto.getRegisterType()) {
                case "wechat":
                    sourceMap.put("公众号", numDto.getNum());
                    break;
                case "routine":
                    sourceMap.put("小程序", numDto.getNum());
                    break;
                case "h5":
                    sourceMap.put("H5", numDto.getNum());
                    break;
                case "iosWx":
                    sourceMap.put("微信ios", numDto.getNum());
                    break;
                case "androidWx":
                    sourceMap.put("微信安卓", numDto.getNum());
                    break;
                case "ios":
                    sourceMap.put("ios", numDto.getNum());
                    break;
            }
        }
        response.setMemberSourceMap(sourceMap);
        return response;
    }

    /**
     * 商户会员环比数据
     * 环比增长率=(本期数-上期数)/上期数×100%
     */
    @Override
    public MerchantMemberMomDataResponse momData(StatisticsDateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        DateTime startDateTime = DateUtil.parse(dateLimit.getStartTime());
        DateTime endDateTime = DateUtil.parse(dateLimit.getEndTime());
        List<DateTime> dateTimeList = DateUtil.rangeToList(startDateTime, endDateTime, DateField.DAY_OF_MONTH);
        DateTime compareStartDateTime = DateUtil.offsetDay(startDateTime, -dateTimeList.size());
        DateTime compareEndDateTime = DateUtil.offsetDay(endDateTime, -dateTimeList.size());

        MerchantMemberMomDataResponse response = new MerchantMemberMomDataResponse();
        double growthRate;
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00%");

        Integer newNum = merchantMemberService.getNewCountByMerIdAndDate(admin.getMerId(), startDateTime.toString(), endDateTime.toString());
        Integer compareNewNum = merchantMemberService.getNewCountByMerIdAndDate(admin.getMerId(), compareStartDateTime.toString(), compareEndDateTime.toString());
        String newNumMom;
        if (newNum <= 0) {
            if (compareNewNum <= 0) {
                newNumMom = "0.00%";
            } else {
                newNumMom = "-100.00%";
            }
        } else {
            growthRate = (newNum.doubleValue() - compareNewNum.doubleValue()) / newNum.doubleValue();
            newNumMom = df.format(growthRate);
        }

        response.setNewNum(newNum);
        response.setNewNumMom(newNumMom);

        BigDecimal rechargeAmount = shoppingCreditsOrderService.getRechargeAmountByMerIdAndDate(admin.getMerId(), startDateTime.toString(), endDateTime.toString());
        BigDecimal compareRechargeAmount = shoppingCreditsOrderService.getRechargeAmountByMerIdAndDate(admin.getMerId(), compareStartDateTime.toString(), compareEndDateTime.toString());
        String rechargeAmountMom;
        if (rechargeAmount.compareTo(BigDecimal.ZERO) == 0) {
            if (compareRechargeAmount.compareTo(BigDecimal.ZERO) == 0) {
                rechargeAmountMom = "0.00%";
            } else {
                rechargeAmountMom = "-100.00%";
            }
        } else {
            BigDecimal rechargeDivide = rechargeAmount.subtract(compareRechargeAmount).divide(rechargeAmount, 4, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN);
            rechargeAmountMom = rechargeDivide.toString() + "%";
        }
        response.setRechargeAmount(rechargeAmount);
        response.setRechargeAmountMom(rechargeAmountMom);

        Integer rechargeOrderNum = shoppingCreditsOrderService.getRechargeOrderNumByMerIdAndDate(admin.getMerId(), startDateTime.toString(), endDateTime.toString());
        Integer compareRechargeOrderNum = shoppingCreditsOrderService.getRechargeOrderNumByMerIdAndDate(admin.getMerId(), compareStartDateTime.toString(), compareEndDateTime.toString());
        String rechargeOrderNumMom;
        if (rechargeOrderNum <= 0) {
            if (compareRechargeOrderNum <= 0) {
                rechargeOrderNumMom = "0.00%";
            } else {
                rechargeOrderNumMom = "-100.00%";
            }
        } else {
            growthRate = (rechargeOrderNum.doubleValue() - compareRechargeOrderNum.doubleValue()) / rechargeOrderNum.doubleValue();
            rechargeOrderNumMom = df.format(growthRate);
        }
        response.setRechargeOrderNum(rechargeOrderNum);
        response.setRechargeOrderNumMom(rechargeOrderNumMom);

        BigDecimal consumeAmount = orderService.getConsumeAmountByMerIdAndDate(admin.getMerId(), startDateTime.toString(), endDateTime.toString());
        BigDecimal compareConsumeAmount = orderService.getConsumeAmountByMerIdAndDate(admin.getMerId(), compareStartDateTime.toString(), compareEndDateTime.toString());
        String consumeAmountMom;
        if (consumeAmount.compareTo(BigDecimal.ZERO) == 0) {
            if (compareConsumeAmount.compareTo(BigDecimal.ZERO) == 0) {
                consumeAmountMom = "0.00%";
            } else {
                consumeAmountMom = "-100.00%";
            }
        } else {
            BigDecimal consumeDivide = consumeAmount.subtract(compareConsumeAmount).divide(consumeAmount, 4, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN);
            consumeAmountMom = consumeDivide.toString() + "%";
        }
        response.setConsumeAmount(consumeAmount);
        response.setConsumeAmountMom(consumeAmountMom);

        Integer consumeOrderNum = orderService.getConsumeOrderNumByMerIdAndDate(admin.getMerId(), startDateTime.toString(), endDateTime.toString());
        Integer compareConsumeOrderNum = orderService.getConsumeOrderNumByMerIdAndDate(admin.getMerId(), compareStartDateTime.toString(), compareEndDateTime.toString());
        String consumeOrderNumMom;
        if (consumeOrderNum <= 0) {
            if (compareConsumeOrderNum <= 0) {
                consumeOrderNumMom = "0.00%";
            } else {
                consumeOrderNumMom = "-100.00%";
            }
        } else {
            growthRate = (consumeOrderNum.doubleValue() - compareConsumeOrderNum.doubleValue()) / consumeOrderNum.doubleValue();
            consumeOrderNumMom = df.format(growthRate);
        }
        response.setConsumeOrderNum(consumeOrderNum);
        response.setConsumeOrderNumMom(consumeOrderNumMom);

        Integer consumeMemberNum = orderService.getConsumeMemberNumByMerIdAndDate(admin.getMerId(), startDateTime.toString(), endDateTime.toString());
        Integer compareConsumeMemberNum = orderService.getConsumeMemberNumByMerIdAndDate(admin.getMerId(), compareStartDateTime.toString(), compareEndDateTime.toString());

        String consumeMemberNumMom;
        if (consumeMemberNum <= 0) {
            if (compareConsumeMemberNum <= 0) {
                consumeMemberNumMom = "0.00%";
            } else {
                consumeMemberNumMom = "-100.00%";
            }
        } else {
            growthRate = (consumeMemberNum.doubleValue() - compareConsumeMemberNum.doubleValue()) / consumeMemberNum.doubleValue();
            consumeMemberNumMom = df.format(growthRate);
        }
        response.setConsumeMemberNum(consumeMemberNum);
        response.setConsumeMemberNumMom(consumeMemberNumMom);

        response.setActiveMemberAmount(consumeMemberNum);
        response.setActiveMemberAmountMom(consumeMemberNumMom);
        return response;
    }

    /**
     * 商户会员新增趋势数据
     */
    @Override
    public List<MerchantMemberNewTrendDataResponse> newMemberTrendData(StatisticsDateRequest request) {
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        String startTime = dateLimit.getStartTime();
        String endTime = dateLimit.getEndTime();
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        List<DateTime> dateTimeList = DateUtil.rangeToList(DateUtil.parse(startTime), DateUtil.parse(endTime), DateField.DAY_OF_MONTH);
        List<MerchantMemberNewTrendDataResponse> responseList = new ArrayList<>();
        for (DateTime dateTime : dateTimeList) {
            Integer memberNum = merchantMemberService.getNewCountByMerIdAndDate(admin.getMerId(), DateUtil.beginOfDay(dateTime).toString(), DateUtil.endOfDay(dateTime).toString());
            Integer rechargeNum = shoppingCreditsOrderService.getRechargeCountByMerIdAndDateGroupUserId(admin.getMerId(), DateUtil.beginOfDay(dateTime).toString(), DateUtil.endOfDay(dateTime).toString());
            MerchantMemberNewTrendDataResponse response = new MerchantMemberNewTrendDataResponse();
            response.setDate(dateTime.toString("MM.dd"));
            response.setNewNum(memberNum);
            response.setRechargeNum(rechargeNum);
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 商户会员消费趋势数据
     */
    @Override
    public List<MerchantMemberConsumeTrendDataResponse> consumeTrendData(StatisticsDateRequest request) {
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        String startTime = dateLimit.getStartTime();
        String endTime = dateLimit.getEndTime();
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        List<DateTime> dateTimeList = DateUtil.rangeToList(DateUtil.parse(startTime), DateUtil.parse(endTime), DateField.DAY_OF_MONTH);
        List<MerchantMemberConsumeTrendDataResponse> responseList = new ArrayList<>();
        for (DateTime dateTime : dateTimeList) {
            Integer orderNum = orderService.getOrderNumByDate(admin.getMerId(), dateTime.toDateStr());
            BigDecimal consumeAmount = orderService.getPayOrderAmountByDate(admin.getMerId(), dateTime.toDateStr());
            MerchantMemberConsumeTrendDataResponse response = new MerchantMemberConsumeTrendDataResponse();
            response.setDate(dateTime.toString("MM.dd"));
            response.setOrderNum(orderNum);
            response.setConsumeAmount(consumeAmount);
            responseList.add(response);
        }
        return responseList;
    }
}
