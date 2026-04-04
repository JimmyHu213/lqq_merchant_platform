package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 可预约日期请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CanReservationDateRequest", description = "可预约日期请求对象")
public class CanReservationDateRequest implements Serializable {

    private static final long serialVersionUID = -891944327432641213L;

    @ApiModelProperty(value = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Integer proId;

    @ApiModelProperty(value = "商品SkuID")
    @NotNull(message = "商品SkuID不能为空")
    private Integer attValueId;

    @ApiModelProperty(value = "预约年月,2025-08")
    @NotBlank(message = "预约年月不能为空")
    private String yearMoth;
}
