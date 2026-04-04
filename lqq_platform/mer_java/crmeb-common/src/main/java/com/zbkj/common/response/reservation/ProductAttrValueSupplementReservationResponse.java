package com.zbkj.common.response.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductAttrValueSupplementReservationResponse对象", description = "预约商品规格值补充信息对象")
public class ProductAttrValueSupplementReservationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer proId;

    @ApiModelProperty(value = "商品SKU表ID")
    private Integer attValueId;

    @ApiModelProperty(value = "时间段")
    private String timeSlot;

    @ApiModelProperty(value = "库存")
    private Integer stock;
}
