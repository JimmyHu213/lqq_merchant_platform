package com.zbkj.front.controller.circle;

import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CirCleCheckVo;
import com.zbkj.front.request.RegionAreasSearchRequest;
import com.zbkj.front.service.CircleAreasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 圈层-商圈控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/22
 */
@Slf4j
@RestController
@RequestMapping("api/front/circle/areas")
@Api(tags = "圈层-商圈控制器")
public class CircleAreasController {

    @Autowired
    private CircleAreasService circleAreasService;

    @ApiOperation(value = "获取商圈列表-经纬度")
    @RequestMapping(value = "/list/location", method = RequestMethod.GET)
    public CommonResult<List<Circle>> findListByLocation(@ModelAttribute @Validated RegionAreasSearchRequest request) {
        return CommonResult.success(circleAreasService.findListByLocation(request));
    }

    @ApiOperation(value = "获取商圈列表-地址")
    @RequestMapping(value = "/list/address", method = RequestMethod.GET)
    public CommonResult<List<Circle>> findListByAddress(@ModelAttribute @Validated RegionAreasSearchRequest request) {
        return CommonResult.success(circleAreasService.findListByAddress(request));
    }

    @ApiOperation(value = "获取商圈树")
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public CommonResult<List<CirCleCheckVo>> getCircleTree() {
        return CommonResult.success(circleAreasService.getCircleTree());
    }
}
