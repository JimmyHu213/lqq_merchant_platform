package com.zbkj.common.model.circle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 圈层身份表
 * </p>
 *
 * @author zzp
 * @since 2025-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_circle_role")
@ApiModel(value="CircleRole对象", description="圈层身份表")
public class CircleRole implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "身份管理id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "身份管理名称")
    private String roleName;

    @ApiModelProperty(value = "身份管理权限(menus_id)")
    private String rules;

    private Integer level;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "管理员类型：5=圈层超管，6=圈层管理员")
    private Integer type;

    @ApiModelProperty(value = "圈层id")
    private Integer circleId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
