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
@ApiModel(value = "MerchantReservationResponse对象", description = "商户预约设置信息响应对象")
public class MerchantReservationToHomeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "抢单开关：0-关闭，1-开启")
    private Boolean vieSwitch;

    @ApiModelProperty(value = "打卡类型：1-自动打卡，2-人工打卡")
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

}
