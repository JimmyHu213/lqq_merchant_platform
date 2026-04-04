package com.zbkj.common.model.merchant;

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
 * 商户信息表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_info")
@ApiModel(value = "MerchantInfo对象", description = "商户信息表")
public class MerchantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户信息ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "结算类型:bank-银行卡,wechat-微信,alipay-支付宝")
    private String settlementType;

    @ApiModelProperty(value = "持卡人姓名")
    private String bankUserName;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行卡号")
    private String bankCard;

    @ApiModelProperty(value = "开户地址")
    private String bankAddress;

    @ApiModelProperty(value = "微信号")
    private String wechatCode;

    @ApiModelProperty(value = "微信收款二维码")
    private String wechatQrcodeUrl;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayCode;

    @ApiModelProperty(value = "支付宝收款二维码")
    private String alipayQrcodeUrl;

    @ApiModelProperty(value = "警戒库存")
    private Integer alertStock;

    @ApiModelProperty(value = "客服类型：H5-H5链接、phone-电话")
    private String serviceType;

    @ApiModelProperty(value = "客服H5链接")
    private String serviceLink;

    @ApiModelProperty(value = "客服电话")
    private String servicePhone;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "抢单开关：0-关闭，1-开启")
    private Boolean vieSwitch;

    @ApiModelProperty(value = "打卡类型：1-无需打卡，2-人工打卡")
    private Integer clockInType;

    @ApiModelProperty(value = "打卡地址开关：0-关闭，1-开启")
    private Boolean clockInAddressSwitch;

    @ApiModelProperty(value = "打卡范围（米）")
    private Integer clockInDistance;

    @ApiModelProperty(value = "拍照备注开关：0-关闭，1-开启")
    private Boolean clockInPhotoSwitch;

    @ApiModelProperty(value = "服务过程凭证开关：0-关闭，1-开启")
    private Boolean serviceEvidenceSwitch;

    @ApiModelProperty(value = "服务过程凭证系统表单ID")
    private Integer serviceEvidenceFormId;

    @ApiModelProperty(value = "上门服务区域开关：0-关闭，1-开启")
    private Boolean serviceAreaSwitch;

    @ApiModelProperty(value = "上门服务区域范围json")
    private String serviceAreaRange;

    @ApiModelProperty(value = "到店服务工单开关：0-关闭，1-开启")
    private Boolean serviceWorkOrderSwitch;
}
