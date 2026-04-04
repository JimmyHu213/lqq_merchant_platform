package com.zbkj.front.controller;

import com.zbkj.common.request.BaseUploadRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.FileResultVo;
import com.zbkj.service.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * 上传文件 前端控制器 -- 用户端
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
@RequestMapping("api/front/upload")
@Api(tags = "用户上传文件")
public class UserUploadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation(value = "图片上传")
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public CommonResult<FileResultVo> image(MultipartFile multipart) {

        return CommonResult.success(uploadService.imageUploadV2(multipart, 0, -2));
    }

    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public CommonResult<FileResultVo> file(MultipartFile multipart) {
        return CommonResult.success(uploadService.fileUploadV2(multipart, 0, -2));
    }

    @ApiOperation(value = "base64图片上传")
    @RequestMapping(value = "/base64", method = RequestMethod.POST)
    public CommonResult<FileResultVo> base64Upload(@RequestBody @Validated BaseUploadRequest request) {
        return CommonResult.success(uploadService.base64UploadV2(request.getBase64Url()));
    }
}



