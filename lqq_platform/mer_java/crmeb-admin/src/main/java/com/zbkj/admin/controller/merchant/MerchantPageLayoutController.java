package com.zbkj.admin.controller.merchant;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.BottomNavigationVo;
import com.zbkj.service.service.PageLayoutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商户页面布局管理
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/19
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/page/layout")
@Api(tags = "商户页面布局管理")
public class MerchantPageLayoutController {

    @Autowired
    private PageLayoutService pageLayoutService;

    @PreAuthorize("hasAuthority('merchant:page:layout:bottom:navigation')")
    @ApiOperation(value = "页面底部导航")
    @RequestMapping(value = "/bottom/navigation/get", method = RequestMethod.GET)
    public CommonResult<List<BottomNavigationVo>> getBottomNavigation() {
        return CommonResult.success(pageLayoutService.getMerchantBottomNavigation());
    }

    @PreAuthorize("hasAuthority('merchant:page:layout:bottom:navigation:save')")
    @ApiOperation(value = "底部导航保存")
    @RequestMapping(value = "/bottom/navigation/save", method = RequestMethod.POST)
    public CommonResult<Object> bottomNavigationSave(@RequestBody @Validated List<BottomNavigationVo> voList) {
        if (pageLayoutService.merchantBottomNavigationSave(voList)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
