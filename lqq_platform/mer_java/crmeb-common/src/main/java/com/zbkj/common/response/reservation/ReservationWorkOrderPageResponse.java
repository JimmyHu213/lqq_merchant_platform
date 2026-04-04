package com.zbkj.common.response.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ReservationWorkOrderPageResponse对象", description="预约工单分页列表响应对象")
public class ReservationWorkOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工单单号")
    private String workOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "预约日期")
    private String reservationDate;

    @ApiModelProperty(value = "预约时段")
    private String reservationTimeSlot;

    @ApiModelProperty(value = "预约人名称")
    private String userName;

    @ApiModelProperty(value = "预约人电话")
    private String userPhone;

    @ApiModelProperty(value = "预约地址")
    private String userAddress;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "服务状态:1-未分配，2-已分配，3-服务中，4-服务结束")
    private Integer serviceStatus;

    @ApiModelProperty(value = "退款状态：0-未退款，1-退款审核中，2-已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "服务类型:1-上门服务，2-到店服务")
    private Integer serviceType;

    @ApiModelProperty(value = "分配类型：0-未分配，1-派单，2-抢单")
    private Integer allocateType;

    @ApiModelProperty(value = "负责人ID")
    private Integer serviceStaffId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "工单备注")
    private String remark;
}
