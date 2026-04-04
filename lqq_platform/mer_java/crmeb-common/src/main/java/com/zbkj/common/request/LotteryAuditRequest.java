package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 抽奖活动审核请求对象
 * [LQQ-迁移] 抽奖系统
 */
@Data
@ApiModel(value = "LotteryAuditRequest", description = "抽奖活动审核请求对象")
public class LotteryAuditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID", required = true)
    @NotNull(message = "活动ID不能为空")
    private Integer activityId;

    @ApiModelProperty(value = "审核结果: 1=通过, 2=拒绝", required = true)
    @NotNull(message = "审核结果不能为空")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因（拒绝时必填）")
    private String reason;
}
