package com.zbkj.common.model.reservation;

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
@ApiModel(value = "TimeSlot对象", description = "预约商品时段信息对象")
public class TimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "时间段: 03:00-04:00",required = true)
    @NotBlank(message = "时段信息时间段不能为空")
    private String timeSlot;

    @ApiModelProperty(value = "是否选中:0-未选中,1-已选中",required = true)
    @NotNull(message = "是否选中不能为空")
    private Integer isCheck;

}
