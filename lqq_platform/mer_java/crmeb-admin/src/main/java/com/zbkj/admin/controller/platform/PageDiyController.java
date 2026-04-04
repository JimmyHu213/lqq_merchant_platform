package com.zbkj.admin.controller.platform;


import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyDefaultResponse;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * DIY数据表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/pagediy")
@Api(tags = "DIY 控制器") //配合swagger使用

public class PageDiyController {

    @Autowired
    private PageDiyService pageDiyService;

    @PreAuthorize("hasAuthority('platform:pagediy:list')")
    @ApiOperation(value = "DIY模板分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "搜索关键子", dataType = "String")
    })
    public CommonResult<CommonPage<PageDiy>> getList(@RequestParam String name, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(pageDiyService.findPlatPage(name, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:pagediy:setdefault')")
    @ApiOperation(value = "设置商城首页")
    @RequestMapping(value = "/setdefault/{id}", method = RequestMethod.GET)
    public CommonResult<String> setDefault(@PathVariable(value = "id") Integer id) {
        if (pageDiyService.setDiyDefault(id)) {
            return CommonResult.success("设置成功");
        }
        return CommonResult.failed("设置失败");
    }

    /**
     * 获取已经设置的首页模版数据
     *
     * @return 首页模版id
     */
    @PreAuthorize("hasAuthority('platform:pagediy:getdefault')")
    @ApiOperation(value = "获取商城首页")
    @RequestMapping(value = "/getdefault", method = RequestMethod.GET)
    public CommonResult<PageDiyDefaultResponse> getDefault() {
        return CommonResult.success(pageDiyService.getDefault());
    }

    @PreAuthorize("hasAuthority('platform:pagediy:save')")
    @ApiOperation(value = "新增DIY模板")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<PageDiy> save(@RequestBody @Validated PageDiyRequest request) {
        return CommonResult.success(pageDiyService.addPageDiy(request));
    }

    @PreAuthorize("hasAuthority('platform:pagediy:delete')")
    @ApiOperation(value = "删除DIY模板")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (pageDiyService.removeById(id)) {
            return CommonResult.success("删除功能");
        }
        return CommonResult.failed("删除失败");
    }

    @PreAuthorize("hasAuthority('platform:pagediy:update')")
    @ApiOperation(value = "编辑DIY模板")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageDiyRequest request) {
        if (pageDiyService.editPageDiy(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('platform:pagediy:updatename')")
    @ApiOperation(value = "DIY 模版名称更新")
    @RequestMapping(value = "/updatename", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageDiyEditNameRequest pageDiyEditNameRequest) {
        if (pageDiyService.editPageDiyName(pageDiyEditNameRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagediy:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(pageDiyService.getDiyPageByPageIdForAdmin(id));
    }
}



