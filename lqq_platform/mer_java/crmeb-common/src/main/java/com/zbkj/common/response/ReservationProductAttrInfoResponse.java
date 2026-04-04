package com.zbkj.common.response;

import com.zbkj.common.model.product.ProductAttribute;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 移动端预约商品规格部分响应对象
 *
 * @author HZW
 * @version 1.0.0
 * @Date 2025/7/9
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ReservationProductAttrInfoResponse", description = "移动端预约商品规格部分响应对象")
public class ReservationProductAttrInfoResponse implements Serializable {

    private static final long serialVersionUID = -226705328599818981L;

    @ApiModelProperty(value = "商品id")
    private Integer id;

    @ApiModelProperty(value = "商户Id")
    private Integer merId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "配送方式：1-商家配送，2-到店核销,3-到店服务，4-上门服务")
    private String deliveryMethod;

    @ApiModelProperty(value = "会员价类型：0-无，1-svip价，2-商户会员价")
    private Integer vipPriceType;

    @ApiModelProperty(value = "产品属性")
    private List<ProductAttribute> attributeList;

    @ApiModelProperty(value = "商品sku-Map对象")
    private HashMap<String, ProductAttrValueResponse> skuMap;

    // ==============预约配置部分===================

    @ApiModelProperty(value = "剩余可约数量展示开关，0-不展示，1-展示")
    private Boolean remainingNumShouSwitch;

    @ApiModelProperty(value = "预约日期天数范围，1-只能预约当天")
    private Integer reservationDateDayRange;

    @ApiModelProperty(value = "取消订单开关：0-不能取消，1-至少提前")
    private Boolean cancelOrderSwitch;

    @ApiModelProperty(value = "取消订单小时数")
    private Integer cancelOrderHour;

    @ApiModelProperty(value = "表单信息处理类型：1-每个预约提交1次，2-每单提交1次")
    private Integer formDataSubmitType;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId;

    @ApiModelProperty(value = "系统表单值")
    private String systemFormValue;

    @ApiModelProperty(value = "提前预约开关：0-不能提前，1-至少提前")
    private Boolean advanceReservationSwitch;

    @ApiModelProperty(value = "提前预约小时数")
    private Integer advanceReservationHour;

    // ==============商户部分===================

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户详细地址")
    private String merchantAddressDetail;

    @ApiModelProperty(value = "商户封面图")
    private String coverImage;

    @ApiModelProperty(value = "纬度")
    private String merchantLatitude;

    @ApiModelProperty(value = "经度")
    private String merchantLongitude;

    @ApiModelProperty(value = "上门服务区域开关：0-关闭，1-开启")
    private Boolean serviceAreaSwitch;

    @ApiModelProperty(value = "上门服务区域范围json,上门服务区域开关开启时有值")
    private String serviceAreaRange;
}
