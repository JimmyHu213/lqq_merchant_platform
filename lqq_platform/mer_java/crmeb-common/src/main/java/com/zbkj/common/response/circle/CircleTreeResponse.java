package com.zbkj.common.response.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@ApiModel(value="CircleTreeResponse", description="圈层树形结构响应对象")
public class CircleTreeResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈层ID")
    private Integer id;

    @ApiModelProperty(value = "圈层名称")
    private String name;

    @ApiModelProperty(value = "圈层父级ID")
    private Integer pid;

    @ApiModelProperty(value = "圈层层级 ：1-一级,2-二级,3-三级")
    private Integer level;

    @ApiModelProperty(value = "圈层范围")
    private String range;

    @ApiModelProperty(value = "显示开关:0-关闭,1=开启")
    private Boolean isShow;

    @ApiModelProperty(value = "圈层开关:0-关闭,1=开启")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "管理员名称")
    private String realName;

    @ApiModelProperty(value = "管理员电话")
    private String phone;

    @ApiModelProperty(value = "DIY模板id")
    private Integer diyId;

    @ApiModelProperty(value = "DIY模板名称")
    private String diyName;

    @ApiModelProperty(value = "圈层子级列表")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CircleTreeResponse> childList;

}
