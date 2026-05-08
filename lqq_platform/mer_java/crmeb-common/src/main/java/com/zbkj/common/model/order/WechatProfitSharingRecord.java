package com.zbkj.common.model.order;

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
 * [LQQ-迁移] 微信多方分账记录表
 * 记录每笔订单的分账明细（锁客商户、推荐人、平台等各方分润）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_profit_sharing_record")
@ApiModel(value = "WechatProfitSharingRecord对象", description = "[LQQ] 微信多方分账记录表")
public class WechatProfitSharingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "平台订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderNo;

    @ApiModelProperty(value = "微信支付订单号(transaction_id)")
    private String transactionId;

    @ApiModelProperty(value = "分账单号(out_order_no)")
    private String profitSharingOrderNo;

    @ApiModelProperty(value = "订单商户ID")
    private Integer orderMerId;

    @ApiModelProperty(value = "消费用户ID")
    private Integer userId;

    @ApiModelProperty(value = "分账接收方用户ID(系统内)")
    private Integer receiverUserId;

    @ApiModelProperty(value = "分账接收方名称")
    private String receiverName;

    @ApiModelProperty(value = "分账接收方微信openid")
    private String receiverOpenid;

    @ApiModelProperty(value = "接收方类型: LOCKED_MERCHANT-锁客商户, REFERRER-推荐人, REFERRER_PARENT-推荐人上级, PROMOTER-提成人, PLATFORM-平台")
    private String receiverType;

    @ApiModelProperty(value = "分账金额(元)")
    private BigDecimal amount;

    @ApiModelProperty(value = "分账比例(%)")
    private BigDecimal rate;

    @ApiModelProperty(value = "分账基数金额(元)")
    private BigDecimal baseAmount;

    @ApiModelProperty(value = "分账描述")
    private String description;

    @ApiModelProperty(value = "分账状态: 0-待分账, 1-分账成功, 2-分账失败, 3-已解冻")
    private Integer status;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @ApiModelProperty(value = "微信子商户号")
    private String subMchId;

    @ApiModelProperty(value = "微信服务商商户号")
    private String serviceMchId;

    // [LQQ-迁移] 佣金冻结与解冻
    @ApiModelProperty(value = "冻结截止时间(到期后可解冻佣金)")
    private Date frozenUntil;

    @ApiModelProperty(value = "是否已解冻佣金: 0=否, 1=已解冻")
    private Integer isUnfrozen;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
