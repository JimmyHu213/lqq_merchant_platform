package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 移动端工单详情
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FrontWorkOrderInfoResponse", description = "移动端工单详情")
public class FrontWorkOrderInfoResponse implements Serializable {

    private static final long serialVersionUID = -1377852980576192005L;

    @ApiModelProperty(value = "工单ID")
    private Integer id;

    @ApiModelProperty(value = "工单单号")
    private String workOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单详情ID")
    private Integer orderDetailId;

    @ApiModelProperty(value = "预约日期")
    private String reservationDate;

    @ApiModelProperty(value = "预约时段")
    private String reservationTimeSlot;

    @ApiModelProperty(value = "预约人ID")
    private Integer userId;

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

    @ApiModelProperty(value = "负责人ID,大于0的情况下，有服务信息")
    private Integer serviceStaffId;

    @ApiModelProperty(value = "协作着IDS")
    private String collaboratorIds;

    @ApiModelProperty(value = "服务状态:1-未分配，2-已分配，3-服务中，4-服务结束")
    private Integer serviceStatus;

    @ApiModelProperty(value = "退款状态：0-未退款，1-退款审核中，2-已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "打卡状态：0-未打卡，1-自动打卡，2-人工打卡")
    private Integer clockInStatus;

    @ApiModelProperty(value = "打卡照片")
    private String clockInPhoto;

    @ApiModelProperty(value = "打卡备注")
    private String clockInRemark;

    @ApiModelProperty(value = "服务过程凭证系统表单ID,大于0的情况下，展示服务留凭")
    private Integer serviceEvidenceFormId;

    @ApiModelProperty(value = "服务过程凭证表单")
    private String serviceEvidenceForm;

    @ApiModelProperty(value = "分配类型：0-未分配，1-派单，2-抢单")
    private Integer allocateType;

    @ApiModelProperty(value = "服务类型:1-上门服务，2-到店服务")
    private Integer serviceType;

    @ApiModelProperty(value = "是否改派")
    private Boolean isEditAllocate;

    @ApiModelProperty(value = "是否改约")
    private Boolean isEditReservation;

    @ApiModelProperty(value = "预约表单ID,大于0的情况下，展示预约表单")
    private Integer reservationFormId;

    @ApiModelProperty(value = "预约表单信息")
    private String reservationFormData;

    @ApiModelProperty(value = "工单备注")
    private String remark;

    @ApiModelProperty(value = "服务开始时间/打卡时间")
    private Date serviceStartTime;

    @ApiModelProperty(value = "服务结束时间")
    private Date serviceEndTime;

    // 订单部分

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "订单用户备注")
    private String userRemark;

    // 服务人员

    @ApiModelProperty(value = "服务员工姓名")
    private String serviceStaffName;

    @ApiModelProperty(value = "服务员工手机号")
    private String serviceStaffPhone;

    @ApiModelProperty(value = "服务时长")
    private String serviceDuration;

    // 系统表单

    @ApiModelProperty(value = "预约系统表单内容")
    private String reservationFormValue = "";

    @ApiModelProperty(value = "服务过程凭证系统表单内容")
    private String serviceEvidenceFormValue = "";
}
