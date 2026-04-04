package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 店铺街请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/11/3
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantStreetRequest", description="店铺街请求对象")
public class MerchantStreetRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = -3043714482394630502L;

    @ApiModelProperty(value = "圈层ID,多个圈层id用逗号分隔")
    private String circleIds;
}
