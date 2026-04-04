package com.zbkj.front.controller;


import com.zbkj.common.request.FrontPageDiyRequest;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * DIY数据表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/front/pagediy")
@Api(tags = "DIY 控制器") //配合swagger使用

public class PageDiyController {

    @Autowired
    private PageDiyService pageDiyService;

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@ModelAttribute @Validated FrontPageDiyRequest request) {
        return CommonResult.success(pageDiyService.getDiyPageByFront(request));
    }
}



