package com.zbkj.admin.controller.circle;

import com.zbkj.admin.service.CircleMerchantManageService;
import com.zbkj.common.model.merchant.MerchantCategory;
import com.zbkj.common.model.merchant.MerchantType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantSearchRequest;
import com.zbkj.common.response.MerchantHeaderNumResponse;
import com.zbkj.common.response.MerchantPageResponse;
import com.zbkj.common.response.MerchantPlatformDetailResponse;
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
 * 圈层端商户控制器
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
@RequestMapping("api/admin/circle/merchant")
@Api(tags = "商户控制器")
public class CircleMerchantManageController {

    @Autowired
    private CircleMerchantManageService circleMerchantManageService;



    @PreAuthorize("hasAuthority('circle:merchant:list')")
    @ApiOperation(value="商户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantPageResponse>> getPageList(@Validated MerchantSearchRequest searchRequest,
                                                                      @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(circleMerchantManageService.getCircleMerchantPage(searchRequest, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('circle:merchant:header:num')")
    @ApiOperation(value="商户分页列表表头数量")
    @RequestMapping(value = "/list/header/num", method = RequestMethod.GET)
    public CommonResult<MerchantHeaderNumResponse> getListHeaderNum(@Validated MerchantSearchRequest searchRequest) {
        return CommonResult.success(circleMerchantManageService.getCircleMerchantNum(searchRequest));
    }

    @PreAuthorize("hasAuthority('circle:merchant:detail')")
    @ApiOperation(value="商户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<MerchantPlatformDetailResponse> getDetail(@PathVariable("id") Integer id) {
        return CommonResult.success(circleMerchantManageService.getCircleMerchantDetail(id));
    }

    @PreAuthorize("hasAuthority('circle:merchant:type:list')")
    @ApiOperation(value="获取商户类型列表")
    @RequestMapping(value = "/type/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantType>> typeList() {
        return CommonResult.success(circleMerchantManageService.allList());
    }

    @PreAuthorize("hasAuthority('circle:merchant:category:list')")
    @ApiOperation(value="获取商户分类列表")
    @RequestMapping(value = "/category/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantCategory>> categoryList() {
        return CommonResult.success(circleMerchantManageService.categoryList());
    }

}
