package com.zbkj.front.controller;

import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonIdsRequest;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.request.PayShoppingCreditsPackageRecordRequest;
import com.zbkj.front.request.PayShoppingCreditsPackageRequest;
import com.zbkj.front.request.ShoppingCreditsOrderRefundApplyRequest;
import com.zbkj.front.request.UserShoppingCreditsRecordPageRequest;
import com.zbkj.front.response.MerchantMemberShoppingCreditsAssetResponse;
import com.zbkj.front.response.UserShoppingCreditsMonthRecordResponse;
import com.zbkj.front.service.FrontShoppingCreditsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户购物金控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/10
 */
@Slf4j
@RestController
@RequestMapping("api/front/merchant/shopping/credits")
@Api(tags = "商户购物金控制器")
public class ShoppingCreditsController {

    @Autowired
    private FrontShoppingCreditsService shoppingCreditsService;

    @ApiOperation(value = "获取购物金套餐列表")
    @RequestMapping(value = "/package/list/{merId}", method = RequestMethod.GET)
    public CommonResult<List<ShoppingCreditsPackage>> findPackageList(@PathVariable(value = "merId") Integer merId) {
        return CommonResult.success(shoppingCreditsService.findPackageList(merId));
    }

    @ApiOperation(value = "获取商户会员购物金资产")
    @RequestMapping(value = "/get/merchant/member/asset/{merId}", method = RequestMethod.GET)
    public CommonResult<MerchantMemberShoppingCreditsAssetResponse> getMerchantMemberAsset(@PathVariable(value = "merId") Integer merId) {
        return CommonResult.success(shoppingCreditsService.getMerchantMemberAsset(merId));
    }

    @ApiOperation(value = "购物金套餐列表-根据id集合加载")
    @RequestMapping(value = "/package/list/ids", method = RequestMethod.GET)
    public CommonResult<List<ShoppingCreditsPackage>> findPackageListByIds(@ModelAttribute @Validated CommonIdsRequest request) {
        return CommonResult.success(shoppingCreditsService.findPackageListByIds(request.getIds()));
    }

    @ApiOperation(value = "购买购物金套餐")
    @RequestMapping(value = "/pay/package", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> payShoppingCreditsPackage(@RequestBody @Validated PayShoppingCreditsPackageRequest request) {
        return CommonResult.success(shoppingCreditsService.payShoppingCreditsPackage(request));
    }

    @ApiOperation(value = "购买购物金记录")
    @RequestMapping(value = "/pay/package/record", method = RequestMethod.POST)
    public CommonResult<CommonPage<ShoppingCreditsOrder>> payShoppingCreditsPackageRecord(@RequestBody @Validated PayShoppingCreditsPackageRecordRequest request) {
        return CommonResult.success(CommonPage.restPage(shoppingCreditsService.payShoppingCreditsPackageRecord(request)));
    }

    @ApiOperation(value = "用户购物金记录分页列表")
    @RequestMapping(value = "/user/record/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<UserShoppingCreditsMonthRecordResponse>> userShoppingCreditsPageRecord(@RequestBody @Validated UserShoppingCreditsRecordPageRequest request) {
        return CommonResult.success(CommonPage.restPage(shoppingCreditsService.userShoppingCreditsPageRecord(request)));
    }

    @ApiOperation(value = "购物金订单退款申请")
    @RequestMapping(value = "/order/refund/apply", method = RequestMethod.POST)
    public CommonResult<String> orderRefundApply(@RequestBody @Validated ShoppingCreditsOrderRefundApplyRequest request) {
        if(shoppingCreditsService.orderRefundApply(request)) {
            return CommonResult.success("退款申请成功");
        }
        return CommonResult.failed("退款申请失败");
    }

    @ApiOperation(value = "购物金退款订单分页列表")
    @RequestMapping(value = "/refund/order/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShoppingCreditsRefundOrder>> shoppingCreditsRefundOrderPage(@ModelAttribute @Validated PayShoppingCreditsPackageRecordRequest request) {
        return CommonResult.success(CommonPage.restPage(shoppingCreditsService.shoppingCreditsRefundOrderPage(request)));
    }
}
