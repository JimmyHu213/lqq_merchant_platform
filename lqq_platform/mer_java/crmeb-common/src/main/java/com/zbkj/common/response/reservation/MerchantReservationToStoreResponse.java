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
@ApiModel(value="MerchantReservationToStoreResponse对象", description="商户到店服务预约设置响应对象")
public class MerchantReservationToStoreResponse implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "到店服务工单开关：0-关闭，1-开启")
    private Boolean serviceWorkOrderSwitch;
}
