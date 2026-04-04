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
@ApiModel(value="ReservationStatisticsResponse对象", description="预约工单统计响应对象")
public class ReservationStatisticsResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当日预约量")
    private Integer count;

    @ApiModelProperty(value = "员工所持工单列表")
    private List<ServiceStaffWorkOrderResponse> list;

}
