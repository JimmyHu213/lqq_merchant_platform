package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.AuditRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.OperationResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PromoterMerchantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端 - 推广员代理审核管理
 * [LQQ-迁移] 推广员代理模式
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/promoter-merchant")
@Api(tags = "平台端 - 推广员代理审核")
public class PlatformPromoterMerchantController {

    @Autowired
    private PromoterMerchantService promoterMerchantService;

    @ApiOperation(value = "所有绑定关系列表（含待审核）")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PromoterMerchant>> list(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(promoterMerchantService.getPlatformList(pageParamRequest)));
    }

    @ApiOperation(value = "审核绑定申请")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<OperationResponse> audit(@RequestBody @Validated AuditRequest request) {
        if (promoterMerchantService.auditBinding(request.getId(), request.getAuditStatus(), request.getReason())) {
            return CommonResult.success(new OperationResponse("审核完成"));
        }
        return CommonResult.failed("审核失败");
    }

    @ApiOperation(value = "强制解绑")
    @RequestMapping(value = "/force-unbind/{id}", method = RequestMethod.POST)
    public CommonResult<OperationResponse> forceUnbind(@PathVariable Integer id) {
        if (promoterMerchantService.forceUnbind(id)) {
            return CommonResult.success(new OperationResponse("已解绑"));
        }
        return CommonResult.failed("解绑失败");
    }
}
