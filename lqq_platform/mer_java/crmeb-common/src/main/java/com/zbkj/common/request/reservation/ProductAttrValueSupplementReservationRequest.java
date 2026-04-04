package com.zbkj.common.request.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductAttrValueSupplementReservationRequest对象", description = "预约商品规格值补充信息对象")
public class ProductAttrValueSupplementReservationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "时间段")
    @NotBlank(message = "规格值时间段不能为空")
    private String timeSlot;

    @ApiModelProperty(value = "库存")
    @NotNull(message = "库存不能为空")
    private Integer stock;
}
