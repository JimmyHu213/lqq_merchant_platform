package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantEmployeeResponse对象", description="员工管理响应对象")
public class MerchantEmployeeResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "员工头像")
    private String avatar;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "关联用户id")
    private Integer uid;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "用户姓名")
    private String userAvatar;

    @ApiModelProperty(value = "用户头像")
    private String userName;

    @ApiModelProperty(value = "权限: 1订单管理，2商品管理，3售后管理，4代客下单，5订单核销，6统计")
    private String role;

    @ApiModelProperty(value = "创建时间")
    private Date creatTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "状态：0关，1开")
    private Integer status;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

}
