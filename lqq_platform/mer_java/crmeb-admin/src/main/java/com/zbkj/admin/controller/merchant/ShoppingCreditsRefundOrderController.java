package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.request.ShoppingCreditsRefundOrderSearchRequest;
import com.zbkj.admin.response.ShoppingCreditsRefundOrderInfoResponse;
import com.zbkj.admin.service.ManageShoppingCreditsRefundOrderService;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.OrderRefundAuditRequest;
import com.zbkj.common.response.ShoppingCreditsRefundOrderPageResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 购物金退款订单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/8
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/shipping/credits/refund/order")
@Api(tags = "购物金退款订单控制器")
public class ShoppingCreditsRefundOrderController {

    @Autowired
    private ManageShoppingCreditsRefundOrderService shoppingCreditsRefundOrderService;

    @PreAuthorize("hasAuthority('merchant:shipping:credits:refund:order:page')")
    @ApiOperation(value = "购物金退款订单分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShoppingCreditsRefundOrderPageResponse>> findPage(@Validated ShoppingCreditsRefundOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(shoppingCreditsRefundOrderService.findPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:shipping:credits:refund:order:info')")
    @ApiOperation(value = "购物金退款订单详情")
    @RequestMapping(value = "/info/{refundOrderNo}", method = RequestMethod.GET)
    public CommonResult<ShoppingCreditsRefundOrderInfoResponse> getInfo(@PathVariable("refundOrderNo") String refundOrderNo) {
        return CommonResult.success(shoppingCreditsRefundOrderService.getInfoByMerchant(refundOrderNo));
    }

    @PreAuthorize("hasAuthority('merchant:shipping:credits:refund:order:audit')")
    @ApiOperation(value = "购物金退款订单审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<String> audit(@RequestBody @Validated OrderRefundAuditRequest request) {
        if (shoppingCreditsRefundOrderService.audit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
