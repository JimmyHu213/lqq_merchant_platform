package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 工单打卡请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WorkOrderClockInRequest", description = "工单打卡请求对象")
public class WorkOrderClockInRequest implements Serializable {

    private static final long serialVersionUID = 4834799298591029504L;

    @ApiModelProperty(value = "工单号")
    @NotBlank(message = "工单号不能为空")
    private String workOrderNo;

    @ApiModelProperty(value = "打卡照片")
    private String clockInPhoto;

    @ApiModelProperty(value = "打卡备注")
    private String clockInRemark;
}
