package com.zbkj.admin.request;

import com.zbkj.admin.vo.MerchantMemberBenefitsVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 保存商户会员权益请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberBenefitsSaveRequest", description = "保存商户会员权益请求对象")
public class MerchantMemberBenefitsSaveRequest implements Serializable {

    private static final long serialVersionUID = -2591205486325774521L;

    @ApiModelProperty(value = "会员权益列表", required = true)
    @NotEmpty(message = "会员权益列表不能为空")
    @Valid
    private List<MerchantMemberBenefitsVo> benefitsList;

}
