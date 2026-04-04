package com.zbkj.service.service;

import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.ProductStatisticsChartResponse;
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

public interface ProductStatisticsService {

    /* ------------------------------------------------平台端------------------------------------------- */
    /**
     * 平台端商品顶部核心数据统计
     * @param dateRequest 日期请求
     */
    List<StatisticsTopResponse> topPlatformData(DateRequest dateRequest);

    /**
     * 平台端商品折线趋势图统计
     * @param dateRequest 日期请求
     */
    List<ProductStatisticsChartResponse> chartPlatformData(DateRequest dateRequest);

    /**
     * 平台端商品分类统计
     * @return 统计饼图响应
     */
    List<StatisticsPieResponse> categoryPlatformData();

    /**
     * 平台端商品类型统计
     * @return 统计饼图响应
     */
    List<StatisticsPieResponse> typePlatformData();

    /* ------------------------------------------------商户端------------------------------------------- */
    /**
     * 商户端商品顶部核心数据统计
     * @param dateRequest 日期请求
     */
    List<StatisticsTopResponse> topMerchantData(DateRequest dateRequest);

    /**
     * 商户端商品折线趋势图统计
     * @param dateRequest 日期请求
     */
    List<ProductStatisticsChartResponse> chartMerchantData(DateRequest dateRequest);

    /**
     * 商户端商品分类统计
     * @return 统计饼图响应
     */
    List<StatisticsPieResponse> categoryMerchantData();

    /**
     * 商户端商品类型统计
     * @return 统计饼图响应
     */
    List<StatisticsPieResponse> typeMerchantData();
}
