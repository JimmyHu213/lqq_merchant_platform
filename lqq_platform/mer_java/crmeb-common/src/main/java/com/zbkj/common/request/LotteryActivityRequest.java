package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 抽奖活动创建/编辑请求对象
 * [LQQ-迁移] 抽奖系统
 */
@Data
@ApiModel(value = "LotteryActivityRequest", description = "抽奖活动创建/编辑请求对象")
public class LotteryActivityRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID（编辑时必填）")
    private Integer id;

    @ApiModelProperty(value = "活动名称", required = true)
    @NotBlank(message = "活动名称不能为空")
    private String name;

    @ApiModelProperty(value = "活动图片")
    private String image;

    @ApiModelProperty(value = "活动规则描述")
    private String description;

    @ApiModelProperty(value = "每次参与所需积分", required = true)
    @NotNull(message = "参与积分不能为空")
    @Min(value = 1, message = "参与积分最小为1")
    private Integer pointsCost;

    @ApiModelProperty(value = "开奖所需人数", required = true)
    @NotNull(message = "开奖人数不能为空")
    @Min(value = 2, message = "开奖人数最少为2")
    private Integer participantThreshold;

    @ApiModelProperty(value = "每期中奖人数", required = true)
    @NotNull(message = "中奖人数不能为空")
    @Min(value = 1, message = "中奖人数最少为1")
    private Integer winnerCount;

    @ApiModelProperty(value = "奖品名称", required = true)
    @NotBlank(message = "奖品名称不能为空")
    private String prizeName;

    @ApiModelProperty(value = "奖品价值")
    private BigDecimal prizeValue;
}
