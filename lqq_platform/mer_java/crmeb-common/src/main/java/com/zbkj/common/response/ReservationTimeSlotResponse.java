package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 预约时段响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ReservationTimeSlotResponse", description = "预约时段响应对象")
public class ReservationTimeSlotResponse implements Serializable {

    private static final long serialVersionUID = 579424349538902267L;

    @ApiModelProperty(value = "sku时段ID")
    private Integer attrValueReservationId;

    @ApiModelProperty(value = "商品SKU表ID")
    private Integer attValueId;

    @ApiModelProperty(value = "时间段")
    private String timeSlot;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "是否可以点击")
    private Boolean isClick = false;
}
