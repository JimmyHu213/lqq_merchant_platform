package com.zbkj.service.service;

import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.OrderStatisticsChartResponse;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;

import java.util.List;

/**
 * ProductStatisticsService 接口
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
public interface OrderStatisticsService {

    /* ------------------------------------------------平台端------------------------------------------- */
    /**
     * 平台端-订单顶部核心数据统计
     * @param dateRequest 日期请求对象
     */
    List<StatisticsTopResponse> topPlatformData(DateRequest dateRequest);

    /**
     * 平台端-订单折线趋势图统计
     * @param dateRequest 日期请求对象
     */
    List<OrderStatisticsChartResponse> chartPlatformData(DateRequest dateRequest);

    /**
     * 平台端-订单类型统计
     */
    List<StatisticsPieResponse> typePlatformData();

    /**
     * 平台端-发货方式统计
     */
    List<StatisticsPieResponse> shippingTypePlatformData();


    /* ------------------------------------------------商户端------------------------------------------- */
    /**
     * 商户端-订单顶部核心数据统计
     * @param dateRequest 日期请求对象
     */
    List<StatisticsTopResponse> topMerchantData(DateRequest dateRequest);

    /**
     * 商户端-订单折线趋势图统计
     * @param dateRequest 日期请求对象
     */
    List<OrderStatisticsChartResponse> chartMerchantData(DateRequest dateRequest);

    /**
     * 商户端-订单类型统计
     */
    List<StatisticsPieResponse> typeMerchantData();

    /**
     * 商户端-发货方式统计
     */
    List<StatisticsPieResponse> shippingTypeMerchantData();
}
