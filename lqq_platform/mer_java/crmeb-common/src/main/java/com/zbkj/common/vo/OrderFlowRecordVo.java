package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单流转记录VO对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderFlowRecordVo", description = "订单流转记录VO对象")
public class OrderFlowRecordVo implements Serializable {

    private static final long serialVersionUID = -9147665881113045307L;

    @ApiModelProperty(value = "记录ID")
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "流转类型:create-创建订单,pay-支付,express-快递,fictitious-虚拟发货,receipt-收货,evaluation-评价订单,fulfilled-完成,edit-编辑,cancel-取消,remove-删除订单")
    private String flowType;

    @ApiModelProperty(value = "流转信息")
    private String flowMessage;

    @ApiModelProperty(value = "操作人ID，0-系统")
    private Integer operatorId;

    @ApiModelProperty(value = "操作人名称")
    private String operatorName;

    @ApiModelProperty(value = "0-系统，1-用户，2-平台管理员，3-商户管理员，4-移动端商户管理员")
    private Integer operatorType;

    @ApiModelProperty(value = "操作时间")
    private Date operatorTime;
}
