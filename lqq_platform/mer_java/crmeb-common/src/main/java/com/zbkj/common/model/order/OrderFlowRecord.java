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
import java.util.Date;

/**
 * <p>
 * 订单流转记录表
 * </p>
 *
 * @author HZW
 * @since 2025-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_order_flow_record")
@ApiModel(value = "OrderFlowRecord对象", description = "订单流转记录表")
public class OrderFlowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "流转类型:create-创建订单,paid-支付,delivery-发货,receipt-收货,complete-完成,cancel-取消,refund-订单已退款")
    private String flowType;

    @ApiModelProperty(value = "流转信息")
    private String flowMessage;

    @ApiModelProperty(value = "操作人ID，0-系统")
    private Integer operatorId;

    @ApiModelProperty(value = "0-系统，1-用户，2-平台管理员，3-商户管理员，4-移动端商户管理员")
    private Integer operatorType;

    @ApiModelProperty(value = "操作时间")
    private Date operatorTime;


}
