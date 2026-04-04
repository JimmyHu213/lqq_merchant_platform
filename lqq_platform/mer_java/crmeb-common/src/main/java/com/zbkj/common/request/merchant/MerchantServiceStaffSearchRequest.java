package com.zbkj.common.request.merchant;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Auther: zzp
 * @Description: 商户服务员工管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantServiceStaffSearchRequest对象", description="商户服务员工查询请求对象")
public class MerchantServiceStaffSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "姓名或者手机号")
    private String keywords;

    @ApiModelProperty(value = "状态：0关，1开")
    private Integer status;
}
