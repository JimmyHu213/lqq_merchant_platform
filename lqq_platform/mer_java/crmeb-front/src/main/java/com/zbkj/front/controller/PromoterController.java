package com.zbkj.front.controller;

import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.response.PromoterCommissionStatsResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PromoterMerchantService;
import com.zbkj.service.service.UserService;
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
}
