package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 工单备注请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WorkOrderRemarkRequest", description = "工单备注请求对象")
public class WorkOrderRemarkRequest implements Serializable {

    private static final long serialVersionUID = 2991837085622436173L;

    @ApiModelProperty(value = "工单号")
    @NotEmpty(message = "工单号不能为空")
    private String workOrderNo;

    @ApiModelProperty(value = "备注内容")
    @NotEmpty(message = "备注内容不能为空")
    private String remark;
}
