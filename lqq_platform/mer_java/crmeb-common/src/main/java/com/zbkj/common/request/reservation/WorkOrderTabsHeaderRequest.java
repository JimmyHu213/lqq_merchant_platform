package com.zbkj.common.request.reservation;

import com.zbkj.common.request.UserCommonSearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WorkOrderTabsHeaderRequest对象", description = "预约工单表头请求对象")
public class WorkOrderTabsHeaderRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工单号")
    private String workOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty("预约类型:1-上门服务，2-到店服务")
    private Integer serviceType;

    @ApiModelProperty(value = "预约时间区间")
    private String dateLimit;

}
