package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.request.ShoppingCreditsPackageSaveRequest;
import com.zbkj.admin.service.ManageShoppingCreditsPackageService;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物金套餐控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/4
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/shipping/credits/package")
@Api(tags = "购物金套餐控制器")
public class ShoppingCreditsPackageController {

    @Autowired
    private ManageShoppingCreditsPackageService shoppingCreditsPackageService;

    @PreAuthorize("hasAuthority('merchant:shipping:credits:package:list')")
    @ApiOperation(value = "购物金套餐列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ShoppingCreditsPackage>> getList() {
        return CommonResult.success(shoppingCreditsPackageService.getList());
    }

    @PreAuthorize("hasAuthority('merchant:shipping:credits:package:add')")
    @ApiOperation(value = "购物金套餐添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ShoppingCreditsPackageSaveRequest request) {
        if (shoppingCreditsPackageService.add(request)) {
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    @PreAuthorize("hasAuthority('merchant:shipping:credits:package:update')")
    @ApiOperation(value = "购物金套餐编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ShoppingCreditsPackageSaveRequest request) {
        if (shoppingCreditsPackageService.updatePackage(request)) {
            return CommonResult.success("编辑成功");
        }
        return CommonResult.failed("编辑失败");
    }

    @PreAuthorize("hasAuthority('merchant:shipping:credits:package:delete')")
    @ApiOperation(value = "购物金套餐删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable("id") Integer id) {
        if (shoppingCreditsPackageService.delete(id)) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @PreAuthorize("hasAuthority('merchant:shipping:credits:package:show:update')")
    @ApiOperation(value = "购物金套餐显示状态变更")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateShowStatus(@PathVariable("id") Integer id) {
        if (shoppingCreditsPackageService.updateShowStatus(id)) {
            return CommonResult.success("变更成功");
        }
        return CommonResult.failed("变更失败");
    }
}
