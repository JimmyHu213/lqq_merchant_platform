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
 * 圈层菜单表
 * </p>
 *
 * @author zzp
 * @since 2025-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_circle_menu")
@ApiModel(value="CircleMenu对象", description="圈层菜单表")
public class CircleMenu implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "icon")
    private String icon;

    @ApiModelProperty(value = "权限标识")
    private String perms;

    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "类型，M-目录，C-菜单，A-按钮")
    private String menuType;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "显示状态")
    private Boolean isShow;

    @ApiModelProperty(value = "是否删除，0-否，1-是")
    private Boolean isDelte;

    @ApiModelProperty(value = "系统菜单类型：6-圈层菜单")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
