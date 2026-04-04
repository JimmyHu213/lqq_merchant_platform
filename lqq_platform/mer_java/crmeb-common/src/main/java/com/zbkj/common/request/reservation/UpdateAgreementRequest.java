package com.zbkj.common.request.reservation;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UpdateAgreementRequest对象", description = "预约工单改约请求对象")
public class UpdateAgreementRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工单号列表", required = true)
    @NotEmpty(message = "请选择工单")
    private List<String> workOrderNoList;

    @ApiModelProperty(value = "预约日期")
    @NotEmpty(message = "预约日期不能为空")
    private String reservationDate;

    @ApiModelProperty(value = "预约时段")
    @NotEmpty(message = "预约时段不能为空")
    private String reservationTimeSlot;

    @ApiModelProperty(value = "预约人名称")
    private String userName;

    @ApiModelProperty(value = "预约人电话")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请输入正确的手机号")
    private String userPhone;

    @ApiModelProperty(value = "预约地址")
    private String userAddress;

    @ApiModelProperty(value = "是否批量改约：true为批量改约，false为单个工单改约")
    private Boolean isBatch;

}
