package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.response.HomeOperatingDataResponse;
import com.zbkj.common.response.PlatformHomeRateResponse;
import com.zbkj.common.response.UserChannelDataResponse;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.CircleMerchantService;
import com.zbkj.service.service.CircleService;
import com.zbkj.service.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 圈层端 主页统计数据 服务实现类
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
public class CircleHomeServiceImpl implements CircleHomeService {

    @Autowired
    private HomeService homeService;
    @Autowired
    private CircleMerchantService circleMerchantService;
    @Autowired
    private CircleService circleService;

    /**
     * 圈层端 首页数据
     */
    @Override
    public PlatformHomeRateResponse indexCircleData() {
        List<Integer> merIds = getMerIds();
        return homeService.indexCircleData(merIds);
    }

    /**
     * 圈层端 经营数据
     */
    @Override
    public HomeOperatingDataResponse operatingCircleData() {
        List<Integer> merIds = getMerIds();
        return homeService.operatingCircleData(merIds);
    }

    /**
     * 圈层端 用户渠道比例
     */
    @Override
    public List<UserChannelDataResponse> getUserChannelData() {
        List<Integer> merIds = getMerIds();
        return homeService.getCircleUserChannelData(merIds);
    }

    /**
     * 获取当前登录圈层以及下级圈层所关联的商户列表
     */
    @Override
    public List<Integer> getMerIds() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer circleId = systemAdmin.getCircleId();
        List<Integer> child = circleService.getCircleChildById(circleId);
        List<Integer> merIdList = circleMerchantService.getMerIdListByCirCleIds(child);
        if (CollUtil.isNotEmpty(merIdList)) {
            return merIdList.stream().distinct().collect(Collectors.toList());
        }
        return CollUtil.newArrayList();
    }
}
