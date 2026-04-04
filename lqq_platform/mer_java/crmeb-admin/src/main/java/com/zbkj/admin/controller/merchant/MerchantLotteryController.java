package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.model.lottery.LotteryRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.LotteryActivityService;
import com.zbkj.service.service.LotteryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation(value = "我的抽奖活动列表")
    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryActivity>> activityList(@ModelAttribute PageParamRequest pageParamRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getMerId, merId);
        lqw.eq(LotteryActivity::getIsDel, false);
        lqw.orderByDesc(LotteryActivity::getId);
        List<LotteryActivity> list = lotteryActivityService.list(lqw);
        return CommonResult.success(CommonPage.restPage(new PageInfo<>(list)));
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
    public CommonResult<String> redeem(@PathVariable Integer recordId) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (lotteryRecordService.redeemPrize(recordId, merId)) {
            return CommonResult.success("兑奖成功");
        }
        return CommonResult.failed("兑奖失败");
    }
}
