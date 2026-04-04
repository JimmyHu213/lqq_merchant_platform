package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 圈层选中VO对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/9/19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CirCleCheckVo", description="圈层选中VO对象")
public class CirCleCheckVo implements Serializable {

    private static final long serialVersionUID = -6899753971825058947L;

    @ApiModelProperty(value = "圈层ID")
    private Integer id;

    @ApiModelProperty(value = "圈层名称")
    private String name;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "子对象列表")
    private List<CirCleCheckVo> childList;
}
