package com.zbkj.common.request.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantReservationToStoreRequest对象", description="商户到店服务预约设置请求对象")
public class MerchantReservationToStoreRequest implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "到店服务工单开关：0-关闭，1-开启")
    @NotNull(message = "到店服务工单开关不能为空")
    private Boolean serviceWorkOrderSwitch;

}
