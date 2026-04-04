package com.zbkj.admin.controller.circle;

import com.zbkj.admin.service.CircleProductCategoryService;
import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.ProCategoryCacheVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 圈层端商品分类控制器
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
@RequestMapping("api/admin/circle/product/category")
@Api(tags = "商品分类控制器") //配合swagger使用
public class CircleProductCategoryController {

    @Autowired
    private CircleProductCategoryService circleProductCategoryService;

    @PreAuthorize("hasAuthority('circle:product:category:list')")
    @ApiOperation(value = "商品分类列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantProductCategory>> getList() {
        return CommonResult.success(circleProductCategoryService.getCircleProductCategoryList());
    }

    @PreAuthorize("hasAuthority('circle:product:category:cache:tree')")
    @ApiOperation(value = "商品分类缓存树")
    @RequestMapping(value = "/cache/tree", method = RequestMethod.GET)
    public CommonResult<List<ProCategoryCacheVo>> getPlatformCacheTree() {
        return CommonResult.success(circleProductCategoryService.getCacheTree());
    }
}
