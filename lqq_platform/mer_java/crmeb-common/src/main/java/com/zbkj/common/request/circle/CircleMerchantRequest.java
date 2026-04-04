package com.zbkj.common.request.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CircleMerchantRequest", description="圈层绑定商户请求对象")
public class CircleMerchantRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈层ID", required = true)
    @NotNull(message = "圈层ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "商户ID列表", required = true)
    //@NotEmpty(message = "商户ID列表不能为空")
    private List<Integer> merchantIds;

}
