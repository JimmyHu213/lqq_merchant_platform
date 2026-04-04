package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通用审核请求对象
 * [LQQ-迁移] 抽奖/推广员等模块共用
 */
@Data
@ApiModel(value = "AuditRequest", description = "通用审核请求对象")
public class AuditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID", required = true)
    @NotNull(message = "记录ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "审核结果: 1=通过, 2=拒绝", required = true)
    @NotNull(message = "审核结果不能为空")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因（拒绝时必填）")
    private String reason;
}
