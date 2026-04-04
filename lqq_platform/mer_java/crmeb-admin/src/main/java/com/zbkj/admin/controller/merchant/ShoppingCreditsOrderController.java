package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.request.ShoppingCreditsOrderSearchRequest;
import com.zbkj.admin.service.ManageShoppingCreditsOrderService;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.ShoppingCreditsOrderPageResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购物金订单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/4
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/shipping/credits/order")
@Api(tags = "购物金订单控制器")
public class ShoppingCreditsOrderController {

    @Autowired
    private ManageShoppingCreditsOrderService shoppingCreditsOrderService;

    @PreAuthorize("hasAuthority('merchant:shipping:credits:order:page')")
    @ApiOperation(value = "购物金订单分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShoppingCreditsOrderPageResponse>> findPage(@Validated ShoppingCreditsOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(shoppingCreditsOrderService.findPage(request)));
    }


}
