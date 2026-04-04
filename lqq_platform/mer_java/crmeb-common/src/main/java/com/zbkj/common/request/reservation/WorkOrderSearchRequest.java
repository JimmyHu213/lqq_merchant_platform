package com.zbkj.common.request.reservation;

import com.zbkj.common.request.UserCommonSearchRequest;
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
@ApiModel(value = "WorkOrderSearchRequest对象", description = "预约工单列表请求对象")
public class WorkOrderSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = -2876902696166882738L;

    @ApiModelProperty(value = "工单号")
    private String workOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty("预约类型:1-上门服务，2-到店服务")
    private Integer serviceType;

    @ApiModelProperty(value = "预约时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "服务状态：0-全部，1-待领取，2-已领取，3-服务中，4-服务结束，9-已退款")
    @NotNull(message = "请选择服务状态")
    private Integer status;



}
