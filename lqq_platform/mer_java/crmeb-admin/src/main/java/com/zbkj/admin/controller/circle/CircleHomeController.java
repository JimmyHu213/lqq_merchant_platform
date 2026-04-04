package com.zbkj.admin.controller.circle;

import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.common.response.HomeOperatingDataResponse;
import com.zbkj.common.response.PlatformHomeRateResponse;
import com.zbkj.common.response.UserChannelDataResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 圈层端-首页统计控制器
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
@RequestMapping("api/admin/circle/statistics/home")
@Api(tags = "首页统计控制器")
public class CircleHomeController {

    @Autowired
    private CircleHomeService circleHomeService;


    @PreAuthorize("hasAuthority('circle:statistics:home:index')")
    @ApiOperation(value = "圈层首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<PlatformHomeRateResponse> indexDate() {
        return CommonResult.success(circleHomeService.indexCircleData());
    }

    @PreAuthorize("hasAuthority('circle:statistics:home:operating')")
    @ApiOperation(value = "圈层经营数据")
    @RequestMapping(value = "/operating/data", method = RequestMethod.GET)
    public CommonResult<HomeOperatingDataResponse> operatingData() {
        return CommonResult.success(circleHomeService.operatingCircleData());
    }

    @PreAuthorize("hasAuthority('circle:statistics:home:channel')")
    @ApiOperation(value = "圈层用户渠道数据")
    @RequestMapping(value = "/channel", method = RequestMethod.GET)
    public CommonResult<List<UserChannelDataResponse>> getChannelData() {
        return CommonResult.success(circleHomeService.getUserChannelData());
    }
}
