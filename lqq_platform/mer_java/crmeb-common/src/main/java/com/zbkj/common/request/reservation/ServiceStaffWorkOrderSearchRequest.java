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
@ApiModel(value="ServiceStaffWorkOrderRequest", description="服务员工所持工单请求对象")
public class ServiceStaffWorkOrderSearchRequest extends UserCommonSearchRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "预约人员姓名或手机号")
    private String reservationKeyword;

    @ApiModelProperty(value = "服务员工ID")
    private Integer staffId;

    @ApiModelProperty(value = "服务类型:1-上门服务，2-到店服务")
    private Integer serviceType;

    @ApiModelProperty(value = "预约日期")
    private String reservationDate;

    @ApiModelProperty(value = "服务状态:2-待服务，3-服务中，4-服务结束，逗号分隔")
    private String serviceStatus;
}
