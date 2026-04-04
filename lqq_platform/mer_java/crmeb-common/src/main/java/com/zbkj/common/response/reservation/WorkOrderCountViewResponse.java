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
@ApiModel(value = "WorkOrderCountViewResponse对象", description = "预约工单-甘特图状态数量响应对象")
public class WorkOrderCountViewResponse implements Serializable {
    private static final long serialVersionUID = -8605913636959651047L;

    @ApiModelProperty(value = "未指派")
    private Integer unReserve = 0;

    @ApiModelProperty(value = "待服务")
    private Integer toService = 0;

    @ApiModelProperty(value = "服务中")
    private Integer servicing = 0;

    @ApiModelProperty(value = "已完成")
    private Integer serviced = 0;

}
