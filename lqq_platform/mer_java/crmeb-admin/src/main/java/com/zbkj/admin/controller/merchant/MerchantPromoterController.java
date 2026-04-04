package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.request.PromoterMerchantBindRequest;
import com.zbkj.common.response.OperationResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.PromoterMerchantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 商户端 - 代理推广员管理
 * [LQQ-迁移] 推广员代理模式
 * 流程: 商户邀请 → 平台审核 → 商户管理
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/promoter")
@Api(tags = "商户端 - 代理推广员管理")
public class MerchantPromoterController {

    @Autowired
    private PromoterMerchantService promoterMerchantService;

    @ApiOperation(value = "邀请推广员代理（提交审核）")
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public CommonResult<OperationResponse> invite(@RequestBody @Validated PromoterMerchantBindRequest request) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (promoterMerchantService.invitePromoter(request.getUid(), merId, request.getCommissionRate())) {
            return CommonResult.success(new OperationResponse("邀请已提交，等待平台审核"));
        }
        return CommonResult.failed("邀请失败");
    }

    @ApiOperation(value = "解除代理")
    @RequestMapping(value = "/dismiss", method = RequestMethod.POST)
    public CommonResult<OperationResponse> dismiss() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (promoterMerchantService.dismissPromoter(merId)) {
            return CommonResult.success(new OperationResponse("已解除代理"));
        }
        return CommonResult.failed("解除失败");
    }

    @ApiOperation(value = "我的代理推广员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PromoterMerchant> info() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        return CommonResult.success(promoterMerchantService.getByMerId(merId));
    }

    @ApiOperation(value = "修改代理佣金比例")
    @RequestMapping(value = "/update/rate", method = RequestMethod.POST)
    public CommonResult<OperationResponse> updateRate(@RequestParam BigDecimal commissionRate) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        if (promoterMerchantService.updateCommissionRate(merId, commissionRate)) {
            return CommonResult.success(new OperationResponse("修改成功"));
        }
        return CommonResult.failed("修改失败");
    }
}
