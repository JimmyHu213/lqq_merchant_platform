package com.zbkj.common.model.reservation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 预约商品属性值补充表
 * </p>
 *
 * @author HZW
 * @since 2025-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_attr_value_supplement_reservation")
@ApiModel(value = "ProductAttrValueSupplementReservation对象", description = "预约商品属性值补充表")
public class ProductAttrValueSupplementReservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer proId;

    @ApiModelProperty(value = "商品SKU表ID")
    private Integer attValueId;

    @ApiModelProperty(value = "时间段")
    private String timeSlot;

    @ApiModelProperty(value = "库存")
    private Integer stock;


}
