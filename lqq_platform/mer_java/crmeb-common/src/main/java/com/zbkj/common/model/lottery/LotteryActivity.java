package com.zbkj.common.model.lottery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 抽奖活动表
 * [LQQ-迁移] 抽奖系统
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_lottery_activity")
@ApiModel(value = "LotteryActivity对象", description = "抽奖活动表")
public class LotteryActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "活动图片")
    private String image;

    @ApiModelProperty(value = "活动规则描述")
    private String description;

    @ApiModelProperty(value = "每次参与所需积分")
    private Integer pointsCost;

    @ApiModelProperty(value = "开奖所需人数")
    private Integer participantThreshold;

    @ApiModelProperty(value = "每期中奖人数")
    private Integer winnerCount;

    @ApiModelProperty(value = "奖品名称")
    private String prizeName;

    @ApiModelProperty(value = "奖品价值")
    private BigDecimal prizeValue;

    @ApiModelProperty(value = "状态: 0=关闭, 1=开启")
    private Integer status;

    @ApiModelProperty(value = "当前期号")
    private Integer currentPeriod;

    @ApiModelProperty(value = "是否删除: 0=否, 1=是")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
