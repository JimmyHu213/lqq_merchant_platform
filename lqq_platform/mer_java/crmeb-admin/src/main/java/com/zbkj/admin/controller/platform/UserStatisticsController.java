package com.zbkj.admin.controller.platform;

import com.zbkj.admin.service.UserStatisticsService;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;
import com.zbkj.common.response.statistcs.UserStatisticsChartResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台端用户统计控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/platform/user/statistics")
@Api(tags = "用户统计-控制器")
public class UserStatisticsController {

    @Autowired
    private UserStatisticsService userStatisticsService;

    @PreAuthorize("hasAuthority('platform:user:statistics:top')")
    @ApiOperation(value = "顶部核心数据统计")
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public CommonResult<List<StatisticsTopResponse>> topData(@Validated DateRequest dateRequest) {
        return CommonResult.success(userStatisticsService.topPlatformData(dateRequest));
    }

    @PreAuthorize("hasAuthority('platform:user:statistics:add')")
    @ApiOperation(value = "新增用户数量统计")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public CommonResult<List<StatisticsPieResponse>> chartUserAddData(@Validated DateRequest dateRequest) {
        return CommonResult.success(userStatisticsService.chartUserAddData(dateRequest));
    }

    @PreAuthorize("hasAuthority('platform:user:statistics:alive')")
    @ApiOperation(value = "活跃用户数量统计")
    @RequestMapping(value = "/alive", method = RequestMethod.GET)
    public CommonResult<List<StatisticsPieResponse>> chartUserAliveData(@Validated DateRequest dateRequest) {
        return CommonResult.success(userStatisticsService.chartUserAliveData(dateRequest));
    }

    @PreAuthorize("hasAuthority('platform:user:statistics:member')")
    @ApiOperation(value = "新增付费会员数量统计")
    @RequestMapping(value = "/member", method = RequestMethod.GET)
    public CommonResult<List<StatisticsPieResponse>> chartUserMemberData(@Validated DateRequest dateRequest) {
        return CommonResult.success(userStatisticsService.chartUserMemberData(dateRequest));
    }

    @PreAuthorize("hasAuthority('platform:user:statistics:bought')")
    @ApiOperation(value = "成交用户数量统计")
    @RequestMapping(value = "/bought", method = RequestMethod.GET)
    public CommonResult<List<UserStatisticsChartResponse>> chartUserBoughtData(@Validated DateRequest dateRequest) {
        return CommonResult.success(userStatisticsService.chartUserBoughtData(dateRequest));
    }

}
