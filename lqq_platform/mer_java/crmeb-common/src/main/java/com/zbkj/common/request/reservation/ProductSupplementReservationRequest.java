package com.zbkj.common.request.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductSupplementReservationRequest对象", description = "预约商品补充信息对象")
public class ProductSupplementReservationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "剩余可约数量展示开关，0-不展示，1-展示",required = true)
    @NotNull(message = "剩余可约数量展示开关不能为空")
    private Boolean remainingNumShouSwitch;

    @ApiModelProperty(value = "可约日期类型：1-每天，2-自定义",required = true)
    @NotNull(message = "可约日期类型不能为空")
    private Integer reservationDateType;

    @ApiModelProperty(value = "可约日期开始日期")
    private String reservationDateStartDate;

    @ApiModelProperty(value = "可约日期结束日期")
    private String reservationDateEndDate;

    @ApiModelProperty(value = "可约日期星期范围，1~7 - 周一~周天")
    private String reservationDateWeekRange;

    @ApiModelProperty(value = "预约日期天数范围，1-只能预约当天",required = true)
    @NotNull(message = "预约日期天数范围不能为空")
    private Integer reservationDateDayRange;

    @ApiModelProperty(value = "提前预约开关：0-不能提前，1-至少提前",required = true)
    @NotNull(message = "提前预约开关不能为空")
    private Boolean advanceReservationSwitch;

    @ApiModelProperty(value = "提前预约小时数")
    private Integer advanceReservationHour;

    @ApiModelProperty(value = "取消订单开关：0-不能取消，1-至少提前",required = true)
    @NotNull(message = "取消订单开关不能为空")
    private Boolean cancelOrderSwitch;

    @ApiModelProperty(value = "取消订单小时数")
    private Integer cancelOrderHour;

    @ApiModelProperty(value = "表单信息处理类型：1-每个预约提交1次，2-每单提交1次",required = true)
    @NotNull(message = "表单信息处理类型不能为空")
    private Integer formDataSubmitType;

    @ApiModelProperty(value = "时段划分类型：1-自动，2-自定义",required = true)
    @NotNull(message = "时段划分类型不能为空")
    private Integer timeSlotClassifyType;

    @ApiModelProperty(value = "时段起止时间,时段自动划分有值")
    private String timeSlotStartEndTime;

    @ApiModelProperty(value = "时段时间跨度,分钟,时段自动划分有值")
    private Integer timeSlotSpan;

}
