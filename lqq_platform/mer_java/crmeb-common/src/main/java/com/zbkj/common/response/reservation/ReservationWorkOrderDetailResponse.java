package com.zbkj.common.response.reservation;

import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ReservationWorkOrderDetailResponse对象", description="预约工单详情信息响应对象")
public class ReservationWorkOrderDetailResponse extends ReservationWorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------- 订单信息 ------
    // ------- 商品信息 ------
    @ApiModelProperty(value = "订单商品信息")
    private OrderDetail orderDetail;
    // -------服务人员信息------
    @ApiModelProperty(value = "负责人信息")
    private MerchantServiceStaff serviceStaff;

    @ApiModelProperty(value = "服务时长")
    private String serviceDuration;

    @ApiModelProperty(value = "协作者列表")
    private List<MerchantServiceStaff> collaboratorStaffs;

    @ApiModelProperty(value = "订单用户备注")
    private String userRemark;

    // 系统表单
    @ApiModelProperty(value = "预约系统表单内容")
    private String reservationFormValue = "";

    @ApiModelProperty(value = "服务过程凭证系统表单内容")
    private String serviceEvidenceFormValue = "";










}
