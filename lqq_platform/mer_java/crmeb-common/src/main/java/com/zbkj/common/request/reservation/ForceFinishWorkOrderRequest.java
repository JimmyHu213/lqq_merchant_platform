package com.zbkj.common.request.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ForceFinishWorkOrderRequest对象", description = "强制完成预约工单请求对象")
public class ForceFinishWorkOrderRequest  implements Serializable {

    private static final long serialVersionUID = -2876902696166882738L;

    @ApiModelProperty(value = "工单号列表", required = true)
    @NotEmpty(message = "请选择工单")
    private List<String> workOrderNoList;

}