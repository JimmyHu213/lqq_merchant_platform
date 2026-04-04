package com.zbkj.common.model.circle;

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
 * 圈层-商户关联表
 * </p>
 *
 * @author zzp
 * @since 2025-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_circle_merchant")
@ApiModel(value="CircleMerchant", description="圈层-商户关联表")
public class CircleMerchant implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "圈层ID")
    private Integer circleId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
