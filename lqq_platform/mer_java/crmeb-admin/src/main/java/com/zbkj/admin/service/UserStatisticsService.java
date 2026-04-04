package com.zbkj.admin.service;

import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;
import com.zbkj.common.response.statistcs.UserStatisticsChartResponse;

import java.util.List;

/**
 * UserStatisticsService 接口
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
public interface UserStatisticsService {

    /* ------------------------------------------------平台端------------------------------------------- */
    List<StatisticsTopResponse> topPlatformData(DateRequest dateRequest);

    List<StatisticsPieResponse> chartUserAddData(DateRequest dateRequest);

    List<StatisticsPieResponse> chartUserAliveData(DateRequest dateRequest);

    List<StatisticsPieResponse> chartUserMemberData(DateRequest dateRequest);

    List<UserStatisticsChartResponse> chartUserBoughtData(DateRequest dateRequest);


    /* ------------------------------------------------商户端------------------------------------------- */
    List<StatisticsTopResponse> topMerchantData(DateRequest dateRequest);

    List<StatisticsPieResponse> chartMerchantUserAddData(DateRequest dateRequest);

    List<StatisticsPieResponse> chartMerchantUserAliveData(DateRequest dateRequest);

    List<StatisticsPieResponse> chartMerchantUserMemberData(DateRequest dateRequest);

    List<UserStatisticsChartResponse> chartMerchantUserBoughtData(DateRequest dateRequest);
}
