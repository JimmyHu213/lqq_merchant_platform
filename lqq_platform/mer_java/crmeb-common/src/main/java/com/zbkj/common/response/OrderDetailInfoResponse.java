package com.zbkj.common.response;

import com.zbkj.common.model.reservation.ReservationWorkOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单细节详情响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderDetailInfoResponse", description = "订单细节详情响应对象")
public class OrderDetailInfoResponse implements Serializable {

    private static final long serialVersionUID = 8326743691840175393L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "发货数量")
    private Integer  deliveryNum;

    @ApiModelProperty(value = "申请退款数量")
    private Integer applyRefundNum;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "是否存在工单")
    private Boolean isExistWork = false;

    @ApiModelProperty(value = "工单列表")
    List<ReservationWorkOrder> workOrderList;

    @ApiModelProperty(value = "订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单，7-预约订单,8-卡次订单")
    private Integer secondType;

    @ApiModelProperty(value = "次卡总次数")
    private Integer verifyTotalTimes;

    @ApiModelProperty(value = "次卡剩余次数")
    private Integer verifyRemainingTimes;

    @ApiModelProperty(value = "次卡预计退款金额")
    private BigDecimal verifyRefundPrice;


}
