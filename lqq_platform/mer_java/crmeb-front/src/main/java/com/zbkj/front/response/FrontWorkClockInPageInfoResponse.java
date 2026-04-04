package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 移动端工单打卡页信息响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FrontWorkClockInPageInfoResponse", description = "移动端工单打卡页信息响应对象")
public class FrontWorkClockInPageInfoResponse implements Serializable {

    private static final long serialVersionUID = -8369227292335050533L;

    @ApiModelProperty(value = "工单单号")
    private String workOrderNo;

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

    @ApiModelProperty(value = "打卡地址开关：0-关闭，1-开启")
    private Boolean clockInAddressSwitch;

    @ApiModelProperty(value = "打卡范围（米）")
    private Integer clockInDistance;

    @ApiModelProperty(value = "拍照备注开关：0-关闭，1-开启")
    private Boolean clockInPhotoSwitch;
}
