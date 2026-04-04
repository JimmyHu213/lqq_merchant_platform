package com.zbkj.common.request.merchant;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantServiceStaffRequest对象", description="商户服务员工请求对象")
public class MerchantServiceStaffRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID-更新时必填")
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "请选择用户")
    private Integer userId;

    @ApiModelProperty(value = "员工姓名")
    @NotNull(message = "员工姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度需在2-20字符之间")
    private String name;

    @ApiModelProperty(value = "员工手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "手机号码格式错误")
    private String phone;

    @ApiModelProperty(value = "证件照")
    private String idPhoto;

    @ApiModelProperty(value = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 9999, message = "排序值不能大于9999")
    private Integer sort;

    @ApiModelProperty(value = "状态:1-开启，0-关闭")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "无效状态值")
    @Max(value = 1, message = "无效状态值")
    private Integer status;

}
