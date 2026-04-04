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
@ApiModel(value = "WorkOrderCountItemResponse对象", description = "预约工单-列表状态数量响应对象")
public class WorkOrderCountItemResponse implements Serializable {

    private static final long serialVersionUID = -8605913636959651047L;

    @ApiModelProperty(value = "总数")
    private Integer all = 0;

    @ApiModelProperty(value = "待领取")
    private Integer unReceive = 0;

    @ApiModelProperty(value = "已领取")
    private Integer received = 0;

    @ApiModelProperty(value = "服务中")
    private Integer inService = 0;

    @ApiModelProperty(value = "服务结束")
    private Integer endService = 0;

    @ApiModelProperty(value = "已退款")
    private Integer refunded = 0;


}
