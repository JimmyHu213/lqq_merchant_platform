package com.zbkj.admin.service.impl;

import com.zbkj.admin.service.UserStatisticsService;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;
import com.zbkj.common.response.statistcs.UserStatisticsChartResponse;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * UserStatisticsService 接口实现
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
public class UserStatisticsServiceImpl implements UserStatisticsService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaidMemberOrderService paidMemberOrderService;
    @Autowired
    private MerchantUserService merchantUserService;
    @Autowired
    private MerchantMemberService merchantMemberService;


    @Override
    public List<StatisticsTopResponse> topPlatformData(DateRequest dateRequest) {
        // 获取上个时间段的数据
        DateRequest lastDateRequest = getLastDateRequest(dateRequest.getStartTime(), dateRequest.getEndTime());

        List<StatisticsTopResponse> responses = initTopData();

        // 当前时间段统计数据
        Map<String, Integer> userFixedData = userService.getFixedUserData();
        Integer addUserNum = userService.getAddUserNum(dateRequest);
        Integer aliveUserNum = userVisitRecordService.getAliveUserNum(dateRequest);
        Integer orderUserNum = orderService.getOrderUserNum(dateRequest);
        Integer paidMemberNum = paidMemberOrderService.getPaidMemberNum(dateRequest);
        // 上个时间段统计数据
        Integer lastAddUserNum = userService.getAddUserNum(lastDateRequest);
        Integer lastAliveUserNum = userVisitRecordService.getAliveUserNum(lastDateRequest);
        Integer lastOrderUserNum = orderService.getOrderUserNum(lastDateRequest);
        Integer lastPaidMemberNum = paidMemberOrderService.getPaidMemberNum(lastDateRequest);
        // 用户数量 0
        responses.get(0).setValue(userFixedData.get("userNum"));
        // 付费会员 1
        responses.get(1).setValue(userFixedData.get("paidMemberNum"));
        // 下单用户 2
        calculateGrowth(responses, 2, orderUserNum, lastOrderUserNum);
        // 活跃用户 3
        calculateGrowth(responses, 3, aliveUserNum, lastAliveUserNum);
        // 新增用户 4
        calculateGrowth(responses, 4, addUserNum, lastAddUserNum);
        // 新增付费会员 5
        calculateGrowth(responses, 5, paidMemberNum, lastPaidMemberNum);
        return responses;
    }

    /**
     * 计算增长率
     * @param responses 响应数据集合
     * @param index 索引位置
     * @param currentValue 当前值
     * @param lastValue 上一个时间段的值
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
     * 初始化顶部核心数据
     * @return 顶部核心数据封装对象集合
     */
    private List<StatisticsTopResponse> initTopData() {
        List<StatisticsTopResponse> responses = new ArrayList<>();
        StatisticsTopResponse res1= new StatisticsTopResponse("用户数量", 0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res2= new StatisticsTopResponse("付费会员", 0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res3= new StatisticsTopResponse("下单用户",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res4= new StatisticsTopResponse("活跃用户",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res5= new StatisticsTopResponse("新增用户",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res6= new StatisticsTopResponse("新增付费会员",0,"0.00%", Boolean.FALSE);
        Collections.addAll(responses,res1,res2,res3,res4,res5,res6);
        return responses;
    }

    /**
     * 新增用户数量统计
     */
    @Override
    public List<StatisticsPieResponse> chartUserAddData(DateRequest dateRequest) {
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<StatisticsPieResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            StatisticsPieResponse response= new StatisticsPieResponse();
            response.setName(date);
            response.setValue(userService.getUserAddNum(date, dateRequest));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 活跃用户数量统计
     */
    @Override
    public List<StatisticsPieResponse> chartUserAliveData(DateRequest dateRequest) {
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<StatisticsPieResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            StatisticsPieResponse response= new StatisticsPieResponse();
            response.setName(date);
            response.setValue(userVisitRecordService.getUserAliveNum(date, dateRequest));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 新增付费会员数量统计
     */
    @Override
    public List<StatisticsPieResponse> chartUserMemberData(DateRequest dateRequest) {
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<StatisticsPieResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            StatisticsPieResponse response= new StatisticsPieResponse();
            response.setName(date);
            response.setValue(paidMemberOrderService.getPaidMemberNumByDate(date, dateRequest));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 成交用户数量统计
     */
    @Override
    public List<UserStatisticsChartResponse> chartUserBoughtData(DateRequest dateRequest) {
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<UserStatisticsChartResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            UserStatisticsChartResponse response= new UserStatisticsChartResponse();
            response.setDate(date);
            Map<String, Integer> map = orderService.getOrderUserNum(date, dateRequest);
            response.setOldUser(map.get("oldUserNum"));
            response.setNewUser(map.get("newUserNum"));
            responses.add(response);
        });
        return responses;
    }

    @Override
    public List<StatisticsTopResponse> topMerchantData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        // 获取上个时间段的数据
        DateRequest lastDateRequest = getLastDateRequest(dateRequest.getStartTime(), dateRequest.getEndTime());

        List<StatisticsTopResponse> responses = initMerchantTopData();
        Integer merchantUserNum = merchantUserService.merchantUserNum(merId);
        Integer merchantMemberNum = merchantMemberService.merchantMemberNum(merId);

        // 当前时间段统计数据
        Integer addUserNum = merchantUserService.getAddUserNum(merId, dateRequest);
        Integer aliveUserNum = merchantUserService.getAliveUserNum(merId, dateRequest);
        Integer orderUserNum = merchantUserService.getOrderUserNum(merId, dateRequest);
        Integer paidMemberNum = merchantMemberService.getPaidMemberNum(merId, dateRequest);
        // 上个时间段统计数据
        Integer lastAddUserNum = merchantUserService.getAddUserNum(merId, lastDateRequest);
        Integer lastAliveUserNum = merchantUserService.getAliveUserNum(merId, lastDateRequest);
        Integer lastOrderUserNum = merchantUserService.getOrderUserNum(merId, lastDateRequest);
        Integer lastPaidMemberNum = merchantMemberService.getPaidMemberNum(merId, lastDateRequest);
        // 商户用户数量 0
        responses.get(0).setValue(merchantUserNum);
        // 商户会员数量 1
        responses.get(1).setValue(merchantMemberNum);
        // 下单用户 2
        calculateGrowth(responses, 2, orderUserNum, lastOrderUserNum);
        // 活跃用户 3
        calculateGrowth(responses, 3, aliveUserNum, lastAliveUserNum);
        // 新增用户 4
        calculateGrowth(responses, 4, addUserNum, lastAddUserNum);
        // 新增付费会员 5
        calculateGrowth(responses, 5, paidMemberNum, lastPaidMemberNum);
        return responses;
    }

    /**
     * 商户新增用户数量统计
     */
    @Override
    public List<StatisticsPieResponse> chartMerchantUserAddData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<StatisticsPieResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            StatisticsPieResponse response= new StatisticsPieResponse();
            response.setName(date);
            response.setValue(merchantUserService.getUserAddNum(merId, date, dateRequest));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 商户活跃用户数量统计
     */
    @Override
    public List<StatisticsPieResponse> chartMerchantUserAliveData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<StatisticsPieResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            StatisticsPieResponse response= new StatisticsPieResponse();
            response.setName(date);
            response.setValue(merchantUserService.getUserAliveNum(merId, date, dateRequest));
            responses.add(response);
        });
        return responses;

    }

    @Override
    public List<StatisticsPieResponse> chartMerchantUserMemberData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<StatisticsPieResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            StatisticsPieResponse response= new StatisticsPieResponse();
            response.setName(date);
            response.setValue(merchantMemberService.getAddMemberNumByDate(merId, date, dateRequest));
            responses.add(response);
        });
        return responses;
    }

    @Override
    public List<UserStatisticsChartResponse> chartMerchantUserBoughtData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<UserStatisticsChartResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            UserStatisticsChartResponse response= new UserStatisticsChartResponse();
            response.setDate(date);
            Map<String, Integer> map = merchantUserService.getOrderMerchantUserNum(merId, date, dateRequest);
            response.setOldUser(map.get("oldUserNum"));
            response.setNewUser(map.get("newUserNum"));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 初始化顶部核心数据
     * @return 顶部核心数据封装对象集合
     */
    private List<StatisticsTopResponse> initMerchantTopData() {
        List<StatisticsTopResponse> responses = new ArrayList<>();
        StatisticsTopResponse res1= new StatisticsTopResponse("用户数量", 0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res2= new StatisticsTopResponse("商户会员", 0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res3= new StatisticsTopResponse("下单用户",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res4= new StatisticsTopResponse("活跃用户",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res5= new StatisticsTopResponse("新增用户",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res6= new StatisticsTopResponse("新增商户会员",0,"0.00%", Boolean.FALSE);
        Collections.addAll(responses,res1,res2,res3,res4,res5,res6);
        return responses;
    }
}
