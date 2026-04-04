package com.zbkj.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 定位对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Location", description="定位对象")
public class Location implements Serializable {

    private static final long serialVersionUID = 9118051367709175377L;

    public Location() {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;
}
