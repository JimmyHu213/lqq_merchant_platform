package com.zbkj.front.controller.staff;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.WorkOrderRemarkRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.request.WorkOrderPageRequest;
import com.zbkj.front.request.WorkOrderAwaitReceiveRequest;
import com.zbkj.front.request.WorkOrderClockInRequest;
import com.zbkj.front.request.WorkOrderServiceEvidenceRequest;
import com.zbkj.front.response.FrontWorkClockInPageInfoResponse;
import com.zbkj.front.response.FrontWorkOrderInfoResponse;
import com.zbkj.front.response.FrontWorkOrderPageDataResponse;
import com.zbkj.front.service.staff.StaffWorkOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 服务员工工单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@RestController
@RequestMapping("api/front/staff/work/order")
@Api(tags = "服务员工工单控制器")
public class StaffWorkOrderController {

    @Autowired
    private StaffWorkOrderService workOrderService;

    @ApiOperation(value = "待领取工单分页列表")
    @RequestMapping(value = "/await/receive/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<FrontWorkOrderPageDataResponse>> awaitReceivePage(@ModelAttribute @Validated WorkOrderAwaitReceiveRequest request) {
        return CommonResult.success(CommonPage.restPage(workOrderService.findAwaitReceivePage(request)));
    }

    @ApiOperation(value = "工单分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<FrontWorkOrderPageDataResponse>> page(@ModelAttribute @Validated WorkOrderPageRequest request) {
        return CommonResult.success(CommonPage.restPage(workOrderService.findPage(request)));
    }

    @ApiOperation(value = "工单详情")
    @RequestMapping(value = "/info/{workOrderNo}", method = RequestMethod.GET)
    public CommonResult<FrontWorkOrderInfoResponse> getInfo(@PathVariable(value = "workOrderNo") String workOrderNo) {
        return CommonResult.success(workOrderService.getInfo(workOrderNo));
    }

    @ApiOperation(value = "领取工单")
    @RequestMapping(value = "/receive/{workOrderNo}", method = RequestMethod.POST)
    public CommonResult<Object> receiveWorkOrder(@PathVariable(value = "workOrderNo") String workOrderNo) {
        if (workOrderService.receiveWorkOrder(workOrderNo)) {
            return CommonResult.success("抢单成功");
        }
        return CommonResult.failed("抢单失败");
    }

    @ApiOperation(value = "获取工单打卡页信息")
    @RequestMapping(value = "/clock/in/page/info/{workOrderNo}", method = RequestMethod.GET)
    public CommonResult<FrontWorkClockInPageInfoResponse> getClockInPageInfo(@PathVariable(value = "workOrderNo") String workOrderNo) {
        return CommonResult.success(workOrderService.getClockInPageInfo(workOrderNo));
    }

    @ApiOperation(value = "工单打卡")
    @RequestMapping(value = "/clock/in", method = RequestMethod.POST)
    public CommonResult<Object> clockIn(@Validated @RequestBody WorkOrderClockInRequest request) {
        if (workOrderService.clockIn(request)) {
            return CommonResult.success("打卡成功");
        }
        return CommonResult.failed("打卡失败");
    }

    @ApiOperation(value = "工单服务过程留凭")
    @RequestMapping(value = "/service/evidence", method = RequestMethod.POST)
    public CommonResult<Object> serviceEvidence(@Validated @RequestBody WorkOrderServiceEvidenceRequest request) {
        if (workOrderService.serviceEvidence(request)) {
            return CommonResult.success("留凭成功");
        }
        return CommonResult.failed("留凭失败");
    }

    @ApiOperation(value = "工单服务结束")
    @RequestMapping(value = "/service/end/{workOrderNo}", method = RequestMethod.POST)
    public CommonResult<Object> serviceEnd(@PathVariable(value = "workOrderNo") String workOrderNo) {
        if (workOrderService.serviceEnd(workOrderNo)) {
            return CommonResult.success("服务完成成功");
        }
        return CommonResult.failed("服务完成失败");
    }

    @ApiOperation(value = "工单备注")
    @RequestMapping(value = "/remark", method = RequestMethod.POST)
    public CommonResult<Object> remark(@Validated @RequestBody WorkOrderRemarkRequest request) {
        if (workOrderService.workRemark(request)) {
            return CommonResult.success("备注成功");
        }
        return CommonResult.failed("备注失败");
    }
}
