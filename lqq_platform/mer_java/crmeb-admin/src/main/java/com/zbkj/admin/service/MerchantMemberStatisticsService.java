package com.zbkj.admin.service;

import com.zbkj.admin.request.StatisticsDateRequest;
import com.zbkj.admin.response.MerchantMemberConsumeTrendDataResponse;
import com.zbkj.admin.response.MerchantMemberMomDataResponse;
import com.zbkj.admin.response.MerchantMemberNewTrendDataResponse;
import com.zbkj.admin.response.MerchantMemberOverviewResponse;

import java.util.List;

/**
 * 商户会员统计服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
public interface MerchantMemberStatisticsService {

    /**
     * 会员概览
     */
    MerchantMemberOverviewResponse overviewData();

    /**
     * 商户会员环比数据
     */
    MerchantMemberMomDataResponse momData(StatisticsDateRequest request);

    /**
     * 商户会员新增趋势数据
     */
    List<MerchantMemberNewTrendDataResponse> newMemberTrendData(StatisticsDateRequest request);

    /**
     * 商户会员消费趋势数据
     */
    List<MerchantMemberConsumeTrendDataResponse> consumeTrendData(StatisticsDateRequest request);
}
