package com.zbkj.common.request.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ReservationWorkOrderAssignRequest对象", description = "预约工单派单改派请求对象")
public class ReservationWorkOrderAssignRequest implements Serializable {
    private static final long serialVersionUID = -2876902696166882738L;

    @ApiModelProperty(value = "工单号列表", required = true)
    @NotEmpty(message = "请选择工单")
    private List<String> workOrderNoList;

    @ApiModelProperty(value = "服务人员ID", required = true)
    @NotNull(message = "请选择服务人员")
    private Integer serviceStaffId;
}
