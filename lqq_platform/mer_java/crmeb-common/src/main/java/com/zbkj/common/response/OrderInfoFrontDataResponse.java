package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单详情移动端数据响应对象
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
@ApiModel(value="OrderInfoFrontDataResponse对象", description="订单详情移动端数据响应对象")
public class OrderInfoFrontDataResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品规格值 ID")
    private Integer attrValueId;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "申请退款数量")
    private Integer applyRefundNum;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;

    @ApiModelProperty(value = "发货数量")
    private Integer  deliveryNum;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品,7-预约商品")
    private Integer productType;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer productMarketingType;

    @ApiModelProperty(value = "拓展文本，例云盘链接")
    private String expand;

    @ApiModelProperty(value = "卡密ID字符串")
    private String cardSecretIds;

    @ApiModelProperty(value = "卡密列表")
    private List<CardSecret> cardSecretList;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "兑换积分")
    private Integer redeemIntegral;

    @ApiModelProperty(value = "商品是否支持退款")
    private Boolean proRefundSwitch;

    @ApiModelProperty(value = "预约日期")
    private String reservationDate;

    @ApiModelProperty(value = "预约时间段")
    private String reservationTimeSlot;

    @ApiModelProperty(value = "预约表单信息")
    private String reservationFormData;

    @TableField(exist = false)
    @ApiModelProperty(value = "工单列表")
    private List<ReservationWorkOrder> workOrderList;

    @TableField(exist = false)
    @ApiModelProperty(value = "预约服务人员列表")
    private List<MerchantServiceStaff> staffList;

    @ApiModelProperty(value = "次卡总次数")
    private Integer verifyTotalTimes;

    @ApiModelProperty(value = "次卡剩余次数")
    private Integer verifyRemainingTimes;

    @ApiModelProperty(value = "次卡生效开始日期")
    private String verifyStartDate;

    @ApiModelProperty(value = "次卡生效结束日期")
    private String verifyEndDate;
}
