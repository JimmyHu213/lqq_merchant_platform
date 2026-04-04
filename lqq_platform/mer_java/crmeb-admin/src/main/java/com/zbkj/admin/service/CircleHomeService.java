package com.zbkj.admin.service;

import com.zbkj.common.response.HomeOperatingDataResponse;
import com.zbkj.common.response.PlatformHomeRateResponse;
import com.zbkj.common.response.UserChannelDataResponse;

import java.util.List;

/**
 * 圈层端 - 首页统计
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
public interface CircleHomeService {

    /**
     * 圈层端 结点首页数据
     */
    PlatformHomeRateResponse indexCircleData();

    /**
     * 圈层端 结点经营数据
     */
    HomeOperatingDataResponse operatingCircleData();

    /**
     * 圈层端 结点用户渠道比例
     */
    List<UserChannelDataResponse> getUserChannelData();


    /**
     * 获取当前登录圈层以及下级圈层所关联的商户列表
     */
    List<Integer> getMerIds();
}
