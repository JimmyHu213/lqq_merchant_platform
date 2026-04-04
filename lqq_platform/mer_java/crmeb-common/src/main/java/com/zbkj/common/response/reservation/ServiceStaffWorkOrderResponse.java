package com.zbkj.common.response.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ServiceStaffWorkOrderResponse对象", description="服务员工所持工单对象")
public class ServiceStaffWorkOrderResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工ID")
    private Integer staffId;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "员工手机号")
    private String phone;

    @ApiModelProperty(value = "工单列表")
    private List<ReservationWorkOrderPageResponse> workOrderList;

}
