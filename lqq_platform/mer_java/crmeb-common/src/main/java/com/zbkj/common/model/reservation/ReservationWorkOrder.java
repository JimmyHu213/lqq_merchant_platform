package com.zbkj.common.model.reservation;

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
 * 预约工单表
 * </p>
 *
 * @author HZW
 * @since 2025-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_reservation_work_order")
@ApiModel(value = "ReservationWorkOrder对象", description = "预约工单表")
public class ReservationWorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "工单单号")
    private String workOrderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

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

    @ApiModelProperty(value = "负责人ID")
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

    @ApiModelProperty(value = "服务过程凭证系统表单ID")
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

    @ApiModelProperty(value = "预约表单ID")
    private Integer reservationFormId;

    @ApiModelProperty(value = "预约表单信息")
    private String reservationFormData;

    @ApiModelProperty(value = "工单备注")
    private String remark;

    @ApiModelProperty(value = "服务开始时间")
    private Date serviceStartTime;

    @ApiModelProperty(value = "服务结束时间")
    private Date serviceEndTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
