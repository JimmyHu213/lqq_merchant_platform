package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 圈层公共搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RegionCommonSearchRequest", description = "圈层公共搜索请求对象")
public class RegionCommonSearchRequest {

    @ApiModelProperty(value = "圈层ID集合，分号分隔")
    private String regionIds;

}
