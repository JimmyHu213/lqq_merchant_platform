package com.zbkj.admin.controller.circle;

import com.zbkj.admin.service.CircleRefundOrderService;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.RefundOrderSearchRequest;
import com.zbkj.common.response.PlatformRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderAdminDetailResponse;
import com.zbkj.common.response.RefundOrderCountItemResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 圈层端退款订单控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/circle/refund/order")
@Api(tags = "退款订单控制器")
public class CircleRefundOrderController {

    @Autowired
    private CircleRefundOrderService circleRefundOrderService;


    @PreAuthorize("hasAuthority('circle:refund:order:list')")
    @ApiOperation(value = "退款订单分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformRefundOrderPageResponse>> getList(@Validated RefundOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(circleRefundOrderService.getCircleRefundOrderPage(request)));
    }

    @PreAuthorize("hasAuthority('circle:refund:order:status:num')")
    @ApiOperation(value = "获取退款订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<RefundOrderCountItemResponse> getOrderStatusNum(@Validated RefundOrderSearchRequest request) {
        return CommonResult.success(circleRefundOrderService.getCircleRefundOrderStatusNum(request));
    }

    @PreAuthorize("hasAuthority('circle:refund:order:detail')")
    @ApiOperation(value = "退款订单详情")
    @RequestMapping(value = "/detail/{refundOrderNo}", method = RequestMethod.GET)
    public CommonResult<RefundOrderAdminDetailResponse> getDetail(@PathVariable(value = "refundOrderNo") String refundOrderNo) {
        return CommonResult.success(circleRefundOrderService.getCircleDetail(refundOrderNo));
    }
}
