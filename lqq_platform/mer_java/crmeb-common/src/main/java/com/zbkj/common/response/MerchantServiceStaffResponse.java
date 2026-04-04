package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantServiceStaffResponse 对象", description = "商户端服务员工列表 响应对象")
public class MerchantServiceStaffResponse {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "员工手机号")
    private String phone;

    @ApiModelProperty(value = "证件照")
    private String idPhoto;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态:1-开启，0-关闭")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
