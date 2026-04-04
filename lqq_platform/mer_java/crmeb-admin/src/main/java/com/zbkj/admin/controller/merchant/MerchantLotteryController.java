package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.model.lottery.LotteryRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.LotteryActivityRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.OperationResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.LotteryActivityService;
import com.zbkj.service.service.LotteryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 商户抽奖管理控制器
 * [LQQ-迁移] 抽奖系统
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/lottery")
@Api(tags = "商户端 - 抽奖管理")
public class MerchantLotteryController {

    @Autowired
    private LotteryActivityService lotteryActivityService;
    @Autowired
    private LotteryRecordService lotteryRecordService;

    @ApiOperation(value = "创建抽奖活动")
    @RequestMapping(value = "/activity/create", method = RequestMethod.POST)
    public CommonResult<OperationResponse> createActivity(@RequestBody @Validated LotteryActivityRequest request) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (lotteryActivityService.createActivity(request, merId)) {
            return CommonResult.success(new OperationResponse("创建成功，等待平台审核"));
        }
        return CommonResult.failed("创建失败");
    }

    @ApiOperation(value = "编辑抽奖活动")
    @RequestMapping(value = "/activity/update", method = RequestMethod.POST)
    public CommonResult<OperationResponse> updateActivity(@RequestBody @Validated LotteryActivityRequest request) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (lotteryActivityService.updateActivity(request, merId)) {
            return CommonResult.success(new OperationResponse("编辑成功，需重新审核"));
        }
        return CommonResult.failed("编辑失败");
    }

    @ApiOperation(value = "我的抽奖活动列表")
    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryActivity>> activityList(@ModelAttribute PageParamRequest pageParamRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        return CommonResult.success(CommonPage.restPage(lotteryActivityService.getMerchantList(merId, pageParamRequest)));
    }

    @ApiOperation(value = "活动详情")
    @RequestMapping(value = "/activity/detail/{id}", method = RequestMethod.GET)
    public CommonResult<LotteryActivity> activityDetail(@PathVariable Integer id) {
        return CommonResult.success(lotteryActivityService.getByIdException(id));
    }

    @ApiOperation(value = "开启/关闭活动")
    @RequestMapping(value = "/activity/switch/{id}", method = RequestMethod.POST)
    public CommonResult<OperationResponse> switchActivity(@PathVariable Integer id) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (lotteryActivityService.switchActivity(id, merId)) {
            return CommonResult.success(new OperationResponse("操作成功"));
        }
        return CommonResult.failed("操作失败");
    }

    @ApiOperation(value = "删除活动")
    @RequestMapping(value = "/activity/delete/{id}", method = RequestMethod.POST)
    public CommonResult<OperationResponse> deleteActivity(@PathVariable Integer id) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (lotteryActivityService.deleteActivity(id, merId)) {
            return CommonResult.success(new OperationResponse("删除成功"));
        }
        return CommonResult.failed("删除失败");
    }

    @ApiOperation(value = "当前期参与者列表")
    @RequestMapping(value = "/participants/{activityId}", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecord>> participants(@PathVariable Integer activityId,
                                                                 @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryRecordService.getParticipants(activityId, pageParamRequest)));
    }

    @ApiOperation(value = "中奖记录列表")
    @RequestMapping(value = "/winner/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecord>> winnerList(@ModelAttribute PageParamRequest pageParamRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryRecord::getMerId, merId);
        lqw.eq(LotteryRecord::getIsWinner, 1);
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        List<LotteryRecord> list = lotteryRecordService.list(lqw);
        return CommonResult.success(CommonPage.restPage(new PageInfo<>(list)));
    }

    @ApiOperation(value = "核销兑奖")
    @RequestMapping(value = "/redeem/{recordId}", method = RequestMethod.POST)
    public CommonResult<OperationResponse> redeem(@PathVariable Integer recordId) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (lotteryRecordService.redeemPrize(recordId, merId)) {
            return CommonResult.success(new OperationResponse("兑奖成功"));
        }
        return CommonResult.failed("兑奖失败");
    }
}
