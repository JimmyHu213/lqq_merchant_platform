package com.zbkj.admin.controller.merchant;


import com.zbkj.common.model.product.ProductUnit;
import com.zbkj.common.request.ProductUnitAddRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商品单位表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/product/unit")
@Api(tags = "商品单位表") //配合swagger使用

public class ProductUnitController {

    @Autowired
    private ProductUnitService productUnitService;

    /**
     * 显示商品单位表
     * @author zzp
     * @since 2025-11-26
     */
    @PreAuthorize("hasAuthority('merchant:product:unit:list')")
    @ApiOperation(value = "商品单位列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ProductUnit>>  getList() {
        return CommonResult.success(productUnitService.getList());
    }

    /**
     * 新增商品单位表
     * @author zzp
     * @since 2025-11-26
     */
    @PreAuthorize("hasAuthority('merchant:product:unit:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ProductUnitAddRequest productUnit) {
        if(productUnitService.saveUnit(productUnit)) {
            return CommonResult.success();
        }
        return CommonResult.failed();

    }

    /**
     * 删除商品单位表
     * @param id Integer
     * @author zzp
     * @since 2025-11-26
     */
    @PreAuthorize("hasAuthority('merchant:product:unit:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if(productUnitService.removeById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改商品单位表
     * @author zzp
     * @since 2025-11-26
     */
    @PreAuthorize("hasAuthority('merchant:product:unit:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductUnitAddRequest productUnit) {
        if(productUnitService.updateUnit(productUnit)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询商品单位表信息
     * @param id Integer
     * @author zzp
     * @since 2025-11-26
     */
    @PreAuthorize("hasAuthority('merchant:product:unit:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductUnit> info(@PathVariable(value = "id") Integer id) {
        ProductUnit productUnit = productUnitService.getById(id);
        return CommonResult.success(productUnit);
    }
}



