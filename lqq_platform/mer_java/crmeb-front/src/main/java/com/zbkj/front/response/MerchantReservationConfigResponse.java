package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商户预约设置响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantReservationConfigResponse", description = "商户预约设置响应对象")
public class MerchantReservationConfigResponse implements Serializable {

    private static final long serialVersionUID = 5848994618722768528L;

    @ApiModelProperty(value = "抢单开关：0-关闭，1-开启")
    private Boolean vieSwitch;

    @ApiModelProperty(value = "打卡类型：1-无需打卡，2-人工打卡")
    private Integer clockInType;

    @ApiModelProperty(value = "打卡地址开关：0-关闭，1-开启")
    private Boolean clockInAddressSwitch;

    @ApiModelProperty(value = "打卡范围（米）")
    private Integer clockInDistance;

    @ApiModelProperty(value = "拍照备注开关：0-关闭，1-开启")
    private Boolean clockInPhotoSwitch;

    @ApiModelProperty(value = "服务过程凭证开关：0-关闭，1-开启")
    private Boolean serviceEvidenceSwitch;

    @ApiModelProperty(value = "服务过程凭证系统表单ID")
    private Integer serviceEvidenceFormId;

    @ApiModelProperty(value = "上门服务区域开关：0-关闭，1-开启")
    private Boolean serviceAreaSwitch;

    @ApiModelProperty(value = "上门服务区域范围json")
    private String serviceAreaRange;

    @ApiModelProperty(value = "到店服务工单开关：0-关闭，1-开启")
    private Boolean serviceWorkOrderSwitch;
}
