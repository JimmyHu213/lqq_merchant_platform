package com.zbkj.common.request.circle;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CircleUpdateRequest", description="圈层修改对象")
public class CircleUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈层ID")
    @NotNull(message = "圈层ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "圈层名称")
    @NotEmpty(message = "圈层名称不能为空")
    @Length(min = 3, max = 20, message = "圈层名称长度须在3到20之间")
    private String name;

    //@ApiModelProperty(value = "父级ID")
    //private Integer pid = 0;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "圈层地址开关：1=开，2-关")
    @NotNull(message = "圈层地址开关不能为空")
    private Integer addressSwitch;

    @ApiModelProperty(value = "圈层地址")
    private String address;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "圈层范围")
    private String range;

    @ApiModelProperty(value = "显示开关:0-关闭,1=开启")
    private Boolean isShow;

    @ApiModelProperty(value = "管理员姓名")
    @NotEmpty(message = "管理员姓名不能为空")
    private String realName;

    @ApiModelProperty(value = "管理员手机号", required = true)
    @NotEmpty(message = "管理员手机号不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请输入正确的手机号")
    private String phone;

    @ApiModelProperty(value = "超管角色", required = true)
    @NotEmpty(message = "超管角色不能为空")
    private String role;

}
