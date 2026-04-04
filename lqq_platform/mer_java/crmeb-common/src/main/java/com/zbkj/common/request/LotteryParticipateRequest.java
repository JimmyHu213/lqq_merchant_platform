package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 参与抽奖请求对象
 * [LQQ-迁移] 抽奖系统
 */
@Data
@ApiModel(value = "LotteryParticipateRequest", description = "参与抽奖请求对象")
public class LotteryParticipateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID", required = true)
    @NotNull(message = "请选择抽奖活动")
    private Integer activityId;
}
