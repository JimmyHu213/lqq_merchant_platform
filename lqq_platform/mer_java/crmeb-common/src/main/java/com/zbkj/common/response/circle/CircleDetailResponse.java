package com.zbkj.common.response.circle;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CircleDetailResponse", description="圈层详情对象")
public class CircleDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈层ID")
    private Integer id;

    @ApiModelProperty(value = "圈层名称")
    private String name;

    @ApiModelProperty(value = "父级ID")
    private Integer pid = 0;

    @ApiModelProperty(value = "圈层层级 ：1-一级,2-二级,3-三级")
    private Integer level;

    @ApiModelProperty(value = "圈层地址")
    private String address;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "圈层范围")
    @TableField("`range`")
    private String range;

    @ApiModelProperty(value = "圈层地址开关：1=开，2-关")
    private Integer addressSwitch;

    @ApiModelProperty(value = "显示开关:0-关闭,1=开启")
    private Boolean isShow;

    @ApiModelProperty(value = "圈层开关:0-关闭,1=开启")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "管理员账号")
    private String account;

    @ApiModelProperty(value = "管理员姓名")
    private String realName;

    @ApiModelProperty(value = "管理员手机号")
    private String phone;

    @ApiModelProperty(value = "超管角色")
    private Integer role;

    @ApiModelProperty(value = "归属商户")
    private List<CircleMerchantResponse> merchantList;

}
