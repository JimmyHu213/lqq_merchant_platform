package com.zbkj.front.controller.employee;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.ReservationWorkOrderDetailResponse;
import com.zbkj.common.response.reservation.ReservationWorkOrderPageResponse;
import com.zbkj.common.response.reservation.WorkOrderCountItemResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.employee.EmployeeWorkOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 移动端 商家管理
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
@RequestMapping("api/front/employee/worker/order")
@Api(tags = "移动端商家管理 - 预约工单控制器")
public class EmployeeWorkOrderController {

    @Autowired
    private EmployeeWorkOrderService employeeWorkOrderService;

    @ApiOperation(value = "预约工单分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ReservationWorkOrderPageResponse>> getList(@Validated WorkOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(employeeWorkOrderService.getWorkOrderListPage(request)));
    }

    @ApiOperation(value = "获取工单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<WorkOrderCountItemResponse> getOrderStatusNum(@Validated WorkOrderTabsHeaderRequest request) {
        return CommonResult.success(employeeWorkOrderService.getWorkOrderStatusNum(request));
    }

    @ApiOperation(value = "工单详情")
    @RequestMapping(value = "/detail/{workOrderNo}", method = RequestMethod.GET)
    public CommonResult<ReservationWorkOrderDetailResponse> getInfo(@PathVariable(value = "workOrderNo") String workOrderNo) {
        return CommonResult.success(employeeWorkOrderService.getInfo(workOrderNo));
    }

    @ApiOperation(value = "商户备注工单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated WorkOrderRemarkRequest request) {
        if (employeeWorkOrderService.merchantMark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "商户派单")
    @RequestMapping(value = "/assign", method = RequestMethod.POST)
    public CommonResult<String> assign(@RequestBody @Validated ReservationWorkOrderAssignRequest request) {
        if (employeeWorkOrderService.assign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "商户改派")
    @RequestMapping(value = "/reassign", method = RequestMethod.POST)
    public CommonResult<String> reassign(@RequestBody @Validated ReservationWorkOrderAssignRequest request) {
        if (employeeWorkOrderService.reassign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "商户改约")
    @RequestMapping(value = "/updateAgreement", method = RequestMethod.POST)
    public CommonResult<String> updateAgreement(@RequestBody @Validated UpdateAgreementRequest request) {
        if (employeeWorkOrderService.updateAgreement(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "商户强制完成工单")
    @RequestMapping(value = "/forceFinish", method = RequestMethod.POST)
    public CommonResult<String> forceFinish(@RequestBody @Validated ForceFinishWorkOrderRequest request) {
        if (employeeWorkOrderService.forceFinish(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }






}
