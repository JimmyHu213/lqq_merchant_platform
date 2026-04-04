package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.request.StatisticsDateRequest;
import com.zbkj.admin.response.MerchantMemberConsumeTrendDataResponse;
import com.zbkj.admin.response.MerchantMemberMomDataResponse;
import com.zbkj.admin.response.MerchantMemberNewTrendDataResponse;
import com.zbkj.admin.response.MerchantMemberOverviewResponse;
import com.zbkj.admin.service.MerchantMemberStatisticsService;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商户会员统计控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@RestController
@RequestMapping("api/admin/merchant/member/statistics")
@Api(tags = "商户会员统计控制器")
public class MerchantMemberStatisticsController {

    @Autowired
    private MerchantMemberStatisticsService merchantMemberStatisticsService;

    @PreAuthorize("hasAuthority('merchant:member:statistics:overview')")
    @ApiOperation(value = "商户会员概览")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    public CommonResult<MerchantMemberOverviewResponse> overviewData() {
        return CommonResult.success(merchantMemberStatisticsService.overviewData());
    }

    @PreAuthorize("hasAuthority('merchant:member:statistics:mom:data')")
    @ApiOperation(value = "商户会员环比数据")
    @RequestMapping(value = "/mom/data", method = RequestMethod.GET)
    public CommonResult<MerchantMemberMomDataResponse> momData(@ModelAttribute @Validated StatisticsDateRequest request) {
        return CommonResult.success(merchantMemberStatisticsService.momData(request));
    }

    @PreAuthorize("hasAuthority('merchant:member:statistics:new:trend:data')")
    @ApiOperation(value = "商户会员新增趋势数据")
    @RequestMapping(value = "/new/trend/data", method = RequestMethod.GET)
    public CommonResult<List<MerchantMemberNewTrendDataResponse>> newMemberTrendData(@ModelAttribute @Validated StatisticsDateRequest request) {
        return CommonResult.success(merchantMemberStatisticsService.newMemberTrendData(request));
    }

    @PreAuthorize("hasAuthority('merchant:member:statistics:consume:trend:data')")
    @ApiOperation(value = "商户会员消费趋势数据")
    @RequestMapping(value = "/consume/trend/data", method = RequestMethod.GET)
    public CommonResult<List<MerchantMemberConsumeTrendDataResponse>> consumeTrendData(@ModelAttribute @Validated StatisticsDateRequest request) {
        return CommonResult.success(merchantMemberStatisticsService.consumeTrendData(request));
    }
}
