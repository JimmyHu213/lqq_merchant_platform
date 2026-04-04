package com.zbkj.admin.controller.merchant;

import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.ProductStatisticsChartResponse;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductStatisticsService;
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
 * 商户端商品统计控制器
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
@RequestMapping("api/admin/merchant/product/statistics")
@Api(tags = "商品统计-控制器") //配合swagger使用
public class MerchantProductStatisticsController {

    @Autowired
    private ProductStatisticsService productStatisticsService;

    @PreAuthorize("hasAuthority('merchant:product:statistics:top')")
    @ApiOperation(value = "顶部核心数据统计")
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public CommonResult<List<StatisticsTopResponse>> topData(@Validated DateRequest dateRequest) {
        return CommonResult.success(productStatisticsService.topMerchantData(dateRequest));
    }

    @PreAuthorize("hasAuthority('merchant:product:statistics:chart')")
    @ApiOperation(value = "折线趋势图统计")
    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    public CommonResult<List<ProductStatisticsChartResponse>> chartData(@Validated DateRequest dateRequest) {
        return CommonResult.success(productStatisticsService.chartMerchantData(dateRequest));
    }

    @PreAuthorize("hasAuthority('merchant:product:statistics:category')")
    @ApiOperation(value = "商品分类统计")
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public CommonResult<List<StatisticsPieResponse>> categoryData() {
        return CommonResult.success(productStatisticsService.categoryMerchantData());
    }

    @PreAuthorize("hasAuthority('merchant:product:statistics:type')")
    @ApiOperation(value = "商品类型统计")
    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public CommonResult<List<StatisticsPieResponse>> typeData() {
        return CommonResult.success(productStatisticsService.typeMerchantData());
    }
}
