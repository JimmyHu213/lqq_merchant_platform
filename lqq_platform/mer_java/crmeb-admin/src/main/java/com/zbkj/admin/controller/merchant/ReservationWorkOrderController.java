package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.service.ManageWorkOrderService;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.*;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户预约工单控制器
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
@RequestMapping("api/admin/merchant/work/order")
@Api(tags = "商户预约工单控制器")
public class ReservationWorkOrderController {

    @Autowired
    private ManageWorkOrderService workOrderService;

    @PreAuthorize("hasAuthority('merchant:workOrder:page:list')")
    @ApiOperation(value = "预约工单分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ReservationWorkOrderPageResponse>> getList(@Validated WorkOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(workOrderService.getWorkOrderListPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:status:num')")
    @ApiOperation(value = "获取工单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<WorkOrderCountItemResponse> getOrderStatusNum(@Validated WorkOrderTabsHeaderRequest request) {
        return CommonResult.success(workOrderService.getWorkOrderStatusNum(request));
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:detail')")
    @ApiOperation(value = "工单详情")
    @RequestMapping(value = "/detail/{workOrderNo}", method = RequestMethod.GET)
    public CommonResult<ReservationWorkOrderDetailResponse> getInfo(@PathVariable(value = "workOrderNo") String workOrderNo) {
        return CommonResult.success(workOrderService.getInfo(workOrderNo));
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:mark')")
    @ApiOperation(value = "商户备注工单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated WorkOrderRemarkRequest request) {
        if (workOrderService.merchantMark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:assign')")
    @ApiOperation(value = "商户派单")
    @RequestMapping(value = "/assign", method = RequestMethod.POST)
    public CommonResult<String> assign(@RequestBody @Validated ReservationWorkOrderAssignRequest request) {
        if (workOrderService.assign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:reassign')")
    @ApiOperation(value = "商户改派")
    @RequestMapping(value = "/reassign", method = RequestMethod.POST)
    public CommonResult<String> reassign(@RequestBody @Validated ReservationWorkOrderAssignRequest request) {
        if (workOrderService.reassign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:updateAgreement')")
    @ApiOperation(value = "商户改约")
    @RequestMapping(value = "/updateAgreement", method = RequestMethod.POST)
    public CommonResult<String> updateAgreement(@RequestBody @Validated UpdateAgreementRequest request) {
        if (workOrderService.updateAgreement(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:forceFinish')")
    @ApiOperation(value = "商户强制完成工单")
    @RequestMapping(value = "/forceFinish", method = RequestMethod.POST)
    public CommonResult<String> forceFinish(@RequestBody @Validated ForceFinishWorkOrderRequest request) {
        if (workOrderService.forceFinish(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:overView')")
    @ApiOperation(value = "预约工单甘特图")
    @RequestMapping(value = "/overView", method = RequestMethod.GET)
    public CommonResult<ReservationStatisticsResponse> overView(@Validated ServiceStaffWorkOrderSearchRequest request) {
        return CommonResult.success(workOrderService.getOverView(request));
    }

    @PreAuthorize("hasAuthority('merchant:workOrder:overview:num')")
    @ApiOperation(value = "工单甘特图各状态数量")
    @RequestMapping(value = "/overView/num", method = RequestMethod.GET)
    public CommonResult<WorkOrderCountViewResponse> getViewNum(@Validated ServiceStaffWorkOrderSearchRequest request) {
        return CommonResult.success(workOrderService.getViewNum(request));
    }
}
