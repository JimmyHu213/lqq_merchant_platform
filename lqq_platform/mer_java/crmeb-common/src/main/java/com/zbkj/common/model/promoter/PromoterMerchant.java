package com.zbkj.common.model.promoter;

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
 * 推广员-商户绑定关系表
 * [LQQ-迁移] 推广员代理模式
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_promoter_merchant")
@ApiModel(value = "PromoterMerchant对象", description = "推广员-商户绑定关系表")
public class PromoterMerchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "推广员用户ID")
    private Integer uid;

    @ApiModelProperty(value = "绑定商户ID")
    private Integer merId;

    @ApiModelProperty(value = "代理佣金比例(%)")
    private BigDecimal commissionRate;

    @ApiModelProperty(value = "状态: 0=停用, 1=启用")
    private Integer status;

    @ApiModelProperty(value = "审核状态: 0=待审核, 1=通过, 2=拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核拒绝原因")
    private String auditReason;

    @ApiModelProperty(value = "是否删除: 0=否, 1=是")
    private Boolean isDel;

    @ApiModelProperty(value = "绑定时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
