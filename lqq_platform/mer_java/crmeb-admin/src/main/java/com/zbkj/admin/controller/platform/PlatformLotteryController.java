package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.model.lottery.LotteryRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.LotteryAuditRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.OperationResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.LotteryActivityService;
import com.zbkj.service.service.LotteryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台抽奖管理控制器
 * [LQQ-迁移] 抽奖系统
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/lottery")
@Api(tags = "平台端 - 抽奖管理")
public class PlatformLotteryController {

    @Autowired
    private LotteryActivityService lotteryActivityService;
    @Autowired
    private LotteryRecordService lotteryRecordService;

    @ApiOperation(value = "活动列表（全平台）")
    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryActivity>> activityList(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryActivityService.getPlatformList(pageParamRequest)));
    }

    @ApiOperation(value = "活动详情")
    @RequestMapping(value = "/activity/detail/{id}", method = RequestMethod.GET)
    public CommonResult<LotteryActivity> activityDetail(@PathVariable Integer id) {
        return CommonResult.success(lotteryActivityService.getByIdException(id));
    }

    @ApiOperation(value = "审核活动")
    @RequestMapping(value = "/activity/audit", method = RequestMethod.POST)
    public CommonResult<OperationResponse> auditActivity(@RequestBody @Validated LotteryAuditRequest request) {
        if (lotteryActivityService.auditActivity(request.getActivityId(), request.getAuditStatus(), request.getReason())) {
            return CommonResult.success(new OperationResponse("审核完成"));
        }
        return CommonResult.failed("审核失败");
    }

    @ApiOperation(value = "强制关闭活动")
    @RequestMapping(value = "/activity/close/{id}", method = RequestMethod.POST)
    public CommonResult<OperationResponse> closeActivity(@PathVariable Integer id) {
        if (lotteryActivityService.forceCloseActivity(id)) {
            return CommonResult.success(new OperationResponse("已关闭"));
        }
        return CommonResult.failed("关闭失败");
    }

    @ApiOperation(value = "删除活动")
    @RequestMapping(value = "/activity/delete/{id}", method = RequestMethod.POST)
    public CommonResult<OperationResponse> deleteActivity(@PathVariable Integer id) {
        if (lotteryActivityService.platformDeleteActivity(id)) {
            return CommonResult.success(new OperationResponse("删除成功"));
        }
        return CommonResult.failed("删除失败");
    }

    @ApiOperation(value = "所有抽奖记录")
    @RequestMapping(value = "/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecord>> recordList(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryRecordService.getPlatformRecords(pageParamRequest)));
    }

    @ApiOperation(value = "所有中奖记录")
    @RequestMapping(value = "/winner/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecord>> winnerList(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryRecordService.getPlatformWinners(pageParamRequest)));
    }
}
