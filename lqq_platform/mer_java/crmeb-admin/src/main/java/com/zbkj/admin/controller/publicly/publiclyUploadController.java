package com.zbkj.admin.controller.publicly;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.FileResultVo;
import com.zbkj.service.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 公共上传控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/6/20
 */
@Slf4j
@RestController
@RequestMapping("api/publicly/upload")
@Api(tags = "平台端上传文件")
public class publiclyUploadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation(value = "图片上传")
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "分类ID", defaultValue = "0")
    })
    public CommonResult<FileResultVo> image(MultipartFile multipart, @RequestParam(value = "pid") Integer pid) {
        return CommonResult.success(uploadService.imageUploadV2(multipart, pid));
    }

    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "分类ID", defaultValue = "0")
    })
    public CommonResult<FileResultVo> file(MultipartFile multipart, @RequestParam(value = "pid") Integer pid) {
        return CommonResult.success(uploadService.fileUploadV2(multipart, pid));
    }

}
