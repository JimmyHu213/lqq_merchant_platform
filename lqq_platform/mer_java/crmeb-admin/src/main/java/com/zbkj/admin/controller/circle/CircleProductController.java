package com.zbkj.admin.controller.circle;

import com.zbkj.admin.service.CircleProductService;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PlatProductSearchRequest;
import com.zbkj.common.request.PlatProductTabsHeaderRequest;
import com.zbkj.common.response.PlatformProductListResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.ProductTabsHeaderResponse;
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

import java.util.List;

/**
 * 圈层端商品控制器
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
@RequestMapping("api/admin/circle/product")
@Api(tags = "商品控制器") //配合swagger使用
public class CircleProductController {

    @Autowired
    private CircleProductService circleProductService;


    @PreAuthorize("hasAuthority('circle:product:list')")
    @ApiOperation(value = "商品分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformProductListResponse>> getList(@Validated PlatProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(circleProductService.getAdminList(request)));
    }

    @PreAuthorize("hasAuthority('circle:product:tabs')")
    @ApiOperation(value = "商品表头数量")
    @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
    public CommonResult<List<ProductTabsHeaderResponse>> getTabsHeader(@Validated PlatProductTabsHeaderRequest request) {
        return CommonResult.success(circleProductService.getTabsHeader(request));
    }

    @PreAuthorize("hasAuthority('circle:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(circleProductService.getInfo(id));
    }


}
