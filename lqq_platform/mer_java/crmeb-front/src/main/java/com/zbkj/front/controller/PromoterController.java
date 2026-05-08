package com.zbkj.front.controller;

import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.response.PromoterCommissionStatsResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PromoterMerchantService;
import com.zbkj.service.service.WechatProfitSharingRecordService;
import com.zbkj.service.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推广员 前端控制器
 * [LQQ-迁移] 推广员代理模式
 */
@Slf4j
@RestController
@RequestMapping("api/front/promoter")
@Api(tags = "推广员控制器")
public class PromoterController {

    @Autowired
    private PromoterMerchantService promoterMerchantService;
    @Autowired
    private UserService userService;
    @Autowired
    private WechatProfitSharingRecordService wechatProfitSharingRecordService;

    @ApiOperation(value = "我代理的商户列表")
    @RequestMapping(value = "/my/merchants", method = RequestMethod.GET)
    public CommonResult<List<PromoterMerchant>> myMerchants() {
        Integer uid = userService.getUserIdException();
        return CommonResult.success(promoterMerchantService.getByUid(uid));
    }

    @ApiOperation(value = "佣金统计（代理+裂变分开显示）")
    @RequestMapping(value = "/my/commission/stats", method = RequestMethod.GET)
    public CommonResult<PromoterCommissionStatsResponse> commissionStats() {
        Integer uid = userService.getUserIdException();
        return CommonResult.success(promoterMerchantService.getCommissionStats(uid));
    }

    // [LQQ-迁移] 佣金明细列表：查询当前用户作为接收方的分账记录
    @ApiOperation(value = "我的佣金明细（分账记录）")
    @RequestMapping(value = "/my/commission/detail", method = RequestMethod.GET)
    public CommonResult<IPage<WechatProfitSharingRecord>> commissionDetail(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        Integer uid = userService.getUserIdException();
        if (page < 1) page = 1;
        if (limit > 1000) limit = 1000;
        LambdaQueryWrapper<WechatProfitSharingRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatProfitSharingRecord::getReceiverUserId, uid);
        lqw.orderByDesc(WechatProfitSharingRecord::getCreateTime);
        return CommonResult.success(wechatProfitSharingRecordService.page(new Page<>(page, limit), lqw));
    }
}
