package com.zbkj.admin.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 商户会员权益VO对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberBenefitsVo", description = "商户会员权益VO对象")
public class MerchantMemberBenefitsVo {

    @ApiModelProperty(value = "会员权益ID,编辑必填")
    private Integer id;

    @ApiModelProperty(value = "权益名称")
    @NotBlank(message = "请填写权益名称")
    @Length(max = 8, message = "权益名称最大长度为8个字符")
    private String name;

    @ApiModelProperty(value = "选中图标")
    @NotBlank(message = "选中图标不能为空")
    private String selectedIcon;

    @ApiModelProperty(value = "未选中图标")
    @NotBlank(message = "未选中图标不能为空")
    private String unselectedIcon;

    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "是否能够删除")
    private Boolean canDel;

}
