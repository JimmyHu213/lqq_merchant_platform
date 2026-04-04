package com.zbkj.admin.controller.circle;

import com.zbkj.admin.service.CircleOrderService;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.OrderSearchRequest;
import com.zbkj.common.request.OrderTabsHeaderRequest;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.PlatformOrderAdminDetailResponse;
import com.zbkj.common.response.PlatformOrderPageResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 圈层端订单控制器
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
@RequestMapping("api/admin/circle/order")
@Api(tags = "订单控制器")
public class CircleOrderController {

    @Autowired
    private CircleOrderService circleOrderService;

    @PreAuthorize("hasAuthority('circle:order:list')")
    @ApiOperation(value = "订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformOrderPageResponse>> getList(@Validated OrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(circleOrderService.getCircleOrderPage(request)));
    }

    @PreAuthorize("hasAuthority('circle:order:status:num')")
    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getOrderStatusNum(@Validated OrderTabsHeaderRequest request) {
        return CommonResult.success(circleOrderService.getCircleOrderStatusNum(request));
    }

    @PreAuthorize("hasAuthority('circle:order:info')")
    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PlatformOrderAdminDetailResponse> info(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(circleOrderService.getCircleOrderInfo(orderNo));
    }

}
