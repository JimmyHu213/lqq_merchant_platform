package com.zbkj.front.controller;

import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.model.lottery.LotteryRecord;
import com.zbkj.common.response.LotteryRecordResponse;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.LotteryParticipateRequest;
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
 * 抽奖 前端控制器
 * [LQQ-迁移] 抽奖系统
 */
@Slf4j
@RestController
@RequestMapping("api/front/lottery")
@Api(tags = "抽奖控制器")
public class LotteryController {

    @Autowired
    private LotteryActivityService lotteryActivityService;
    @Autowired
    private LotteryRecordService lotteryRecordService;

    @ApiOperation(value = "抽奖活动列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryActivity>> getList(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryActivityService.getActiveList(pageParamRequest)));
    }

    @ApiOperation(value = "活动详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<LotteryActivity> getDetail(@PathVariable Integer id) {
        return CommonResult.success(lotteryActivityService.getByIdException(id));
    }

    @ApiOperation(value = "参与抽奖")
    @RequestMapping(value = "/participate", method = RequestMethod.POST)
    public CommonResult<OperationResponse> participate(@RequestBody @Validated LotteryParticipateRequest request) {
        if (lotteryRecordService.participate(request.getActivityId())) {
            return CommonResult.success(new OperationResponse("参与成功"));
        }
        return CommonResult.failed("参与失败");
    }

    @ApiOperation(value = "我的抽奖记录")
    @RequestMapping(value = "/my/records", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecordResponse>> getMyRecords(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryRecordService.getMyRecords(pageParamRequest)));
    }

    // [LQQ-迁移] 当前期参与者列表
    @ApiOperation(value = "当前期参与者列表")
    @RequestMapping(value = "/participants/{activityId}", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecordResponse>> getParticipants(@PathVariable Integer activityId,
                                                                    @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryRecordService.getParticipants(activityId, pageParamRequest)));
    }
}
