package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 圈层搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RegionAreasSearchRequest", description = "圈层搜索请求对象")
public class RegionAreasSearchRequest implements Serializable {

    private static final long serialVersionUID = -5818357764445623035L;

    @ApiModelProperty("经度，经纬度搜索必传")
    private String longitude;

    @ApiModelProperty("纬度，经纬度搜索必传")
    private String latitude;

    @ApiModelProperty("距离：km，经纬度搜索必传")
    private Integer distance;

    @ApiModelProperty("用户地址，地址搜索必传")
    private String address;
}
