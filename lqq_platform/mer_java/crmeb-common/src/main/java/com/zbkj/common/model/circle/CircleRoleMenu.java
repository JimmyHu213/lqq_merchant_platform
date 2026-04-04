package com.zbkj.common.model.circle;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 圈层角色菜单关联表
 * </p>
 *
 * @author zzp
 * @since 2025-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_circle_role_menu")
@ApiModel(value="CircleRoleMenu对象", description="圈层角色菜单关联表")
public class CircleRoleMenu implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "角色id")
    private Integer rid;

    @ApiModelProperty(value = "权限id")
    private Integer menuId;


}
