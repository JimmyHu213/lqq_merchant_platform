package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 预下单商户订单Vo对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PreMerchantOrderVo对象", description = "预下单商户订单Vo对象")
public class PreMerchantOrderVo implements Serializable {

    private static final long serialVersionUID = 7282892323898493847L;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商品总数")
    private Integer proTotalNum;

    @ApiModelProperty(value = "商品总计金额")
    private BigDecimal proTotalFee;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "优惠券编号（选择优惠券时有值,不选时为0")
    private Integer userCouponId = 0;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "自提开关:0-关闭，1-开启")
    private Boolean takeTheirSwitch;

    @ApiModelProperty(value = "配送方式 1=快递,2=门店自提,3=虚拟发货,4-到店服务,5-上门服务")
    private Integer shippingType = 1;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货详细地址")
    private String userAddress;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral = 0;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "商户订单详情数组")
    private List<PreOrderInfoDetailVo> orderInfoList;

    @ApiModelProperty(value = "商户优惠金额")
    private BigDecimal merCouponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "商户会员优惠金额")
    private BigDecimal merchantMemberDiscountPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "订单类型:0-基础订单,1-秒杀订单,2-拼团订单")
    private Integer type;

    @ApiModelProperty(value = "订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单,7-预约订单")
    private Integer secondType;

    @ApiModelProperty(value = "商家配送方式：1-商家配送，2-到店核销，,3-到店服务，4-上门服务,逗号分隔")
    private String deliveryMethodMer;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId = 0;

    @ApiModelProperty(value = "系统表单内容")
    private String systemFormValue = "";

    @ApiModelProperty(value = "商品规格属性预约时段id（立即购买预约商品必填）")
    private Integer attrValueReservationId;

    @ApiModelProperty(value = "预约日期（立即购买预约商品必填）")
    private String reservationDate;

    @ApiModelProperty(value = "预约时间段")
    private String reservationTimeSlot;

    @ApiModelProperty(value = "预约订单取消预约开关：0-不能取消，1-至少提前")
    private Boolean cancelServiceOrderSwitch;

    @ApiModelProperty(value = "预约订单取消预约小时数")
    private Integer cancelServiceOrderHour;

    @ApiModelProperty(value = "表单信息处理类型：1-每个预约提交1次，2-每单提交1次")
    private Integer formDataSubmitType;
}
