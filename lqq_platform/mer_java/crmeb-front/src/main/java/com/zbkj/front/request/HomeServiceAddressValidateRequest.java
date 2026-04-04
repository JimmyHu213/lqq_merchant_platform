package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约订单上门服务地址验证请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "HomeServiceAddressValidateRequest", description = "预约订单上门服务地址验证请求对象")
public class HomeServiceAddressValidateRequest implements Serializable {

    private static final long serialVersionUID = 1800880688601139089L;

    @ApiModelProperty(value = "商户id")
    @NotNull(message = "商户ID不能为空")
    private Integer merId;

    @ApiModelProperty(value = "用户地址id")
    @NotNull(message = "用户地址不能为空")
    private Integer userAddressId;

}
