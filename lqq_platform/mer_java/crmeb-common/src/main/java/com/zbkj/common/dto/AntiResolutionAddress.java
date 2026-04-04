package com.zbkj.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 反解析地址对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AntiResolutionAddress", description="反解析地址对象")
public class AntiResolutionAddress implements Serializable {

    private static final long serialVersionUID = 5425949402472816304L;

    public AntiResolutionAddress(String address, String recommend, String standard) {
        this.address = address;
        this.recommendAddress = recommend;
        this.standardAddress = standard;
    }


    @ApiModelProperty("以行政区划+道路+门牌号等信息组成的标准格式化地址")
    private String address;

    @ApiModelProperty("结合知名地点形成的描述性地址,推荐使用的地址描述，描述精确性较高")
    private String recommendAddress;

    @ApiModelProperty("结合知名地点形成的描述性地址,基于附近关键地点（POI）的精确地址")
    private String standardAddress;
}
