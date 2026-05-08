package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽奖记录 Response — 前端列表展示用
 * [LQQ-迁移] 抽奖系统
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotteryRecordResponse对象", description = "抽奖记录响应对象")
public class LotteryRecordResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "活动ID")
    private Integer activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "参与用户ID")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "期号")
    private String periodNumber;

    @ApiModelProperty(value = "消耗积分")
    private Integer pointsCost;

    @ApiModelProperty(value = "是否中奖: 0=否, 1=是")
    private Integer isWinner;

    @ApiModelProperty(value = "是否兑奖: 0=否, 1=已兑")
    private Integer isRedeemed;

    @ApiModelProperty(value = "兑奖时间")
    private Date redeemTime;

    @ApiModelProperty(value = "开奖时间(NULL=未开奖)")
    private Date drawTime;

    @ApiModelProperty(value = "参与时间")
    private Date createTime;
}
