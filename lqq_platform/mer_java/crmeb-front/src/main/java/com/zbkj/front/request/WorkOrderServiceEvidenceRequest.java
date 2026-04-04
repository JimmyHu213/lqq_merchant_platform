package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 工单服务过程留凭请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WorkOrderServiceEvidenceRequest", description = "工单服务过程留凭请求对象")
public class WorkOrderServiceEvidenceRequest implements Serializable {

    private static final long serialVersionUID = 6395950504787745734L;

    @ApiModelProperty(value = "工单号")
    @NotBlank(message = "工单号不能为空")
    private String workOrderNo;

    @ApiModelProperty(value = "服务过程凭证表单")
    @NotBlank(message = "凭证数据不能为空")
    private String serviceEvidenceForm;
}
