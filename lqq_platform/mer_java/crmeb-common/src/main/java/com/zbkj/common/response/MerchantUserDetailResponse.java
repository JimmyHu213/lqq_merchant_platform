package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户用户详情响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantUserDetailResponse", description = "商户用户详情响应对象")
public class MerchantUserDetailResponse implements Serializable {

    private static final long serialVersionUID = 6187205286317436985L;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;


    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "财务状态:0-冻结，1-正常")
    private Integer financialStatus;

    @ApiModelProperty(value = "会员等级")
    private Integer level;

    @ApiModelProperty(value = "会员等级名称")
    private String levelName = "--";

    @ApiModelProperty(value = "累计消费金额")
    private BigDecimal consumeTotalAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计消费单数")
    private Integer consumeTotalOrderNum = 0;


    @ApiModelProperty(value = "用户账号")
    private String account;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "国家，中国CN，其他OTHER")
    private String country;

    @ApiModelProperty(value = "用户地址")
    private String userAddress;

    @ApiModelProperty(value = "注册类型：wechat-公众号，routine-小程序，H5-H5,iosWx-微信ios，androidWx-微信安卓，ios-ios")
    private String registerType;


    @ApiModelProperty(value = "入会时间")
    private Date membershipTime;

    @ApiModelProperty(value = "首次访问时间")
    private Date firstVisitTime;

    @ApiModelProperty(value = "最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "加入商户用户时间")
    private Date addMerchantUserTime;
}
