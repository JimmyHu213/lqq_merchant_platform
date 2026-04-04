package com.zbkj.common.request.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantReservationRequest对象", description="商户预约设置请求对象")
public class MerchantReservationToHomeRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "抢单开关：0-关闭，1-开启")
    @NotNull(message = "抢单开关不能为空")
    private Boolean vieSwitch;

    @ApiModelProperty(value = "打卡类型：1-自动打卡，2-人工打卡")
    @NotNull(message = "打卡类型不能为空")
    @Min(value = 1, message = "无效打卡类型值")
    @Max(value = 2, message = "无效打卡类型值")
    private Integer clockInType;

    @ApiModelProperty(value = "打卡地址开关：0-关闭，1-开启")
    @NotNull(message = "打卡地址开关不能为空")
    private Boolean clockInAddressSwitch;

    @ApiModelProperty(value = "打卡范围（米）")
    @Min(value = 1, message = "打卡范围不能小于1米")
    private Integer clockInDistance;

    @ApiModelProperty(value = "拍照备注开关：0-关闭，1-开启")
    @NotNull(message = "拍照备注开关不能为空")
    private Boolean clockInPhotoSwitch;

    @ApiModelProperty(value = "服务过程凭证开关：0-关闭，1-开启")
    @NotNull(message = "服务过程凭证开关不能为空")
    private Boolean serviceEvidenceSwitch;

    @ApiModelProperty(value = "服务过程凭证系统表单ID")
    private Integer serviceEvidenceFormId;

    @ApiModelProperty(value = "上门服务区域开关：0-关闭，1-开启")
    @NotNull(message = "上门服务区域开关不能为空")
    private Boolean serviceAreaSwitch;

    @ApiModelProperty(value = "上门服务区域范围json")
    @Size(max = 2000, message = "服务区域范围JSON数据过长")
    private String serviceAreaRange;

}
