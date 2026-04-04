package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户Excel VO对象
 * @Author HZW
 * @Date 2025/6/24 12:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserExcelVo", description = "用户Excel VO对象")
public class UserExcelVo implements Serializable {

    private static final long serialVersionUID = -8330957183745338822L;

    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "标签id,英文逗号分隔")
    private String tags;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "国家，中国CN，其他OTHER")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private String sex;

    @ApiModelProperty(value = "用户积分")
    private Integer integral;

    @ApiModelProperty(value = "用户经验")
    private Integer experience;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "佣金金额")
    private BigDecimal brokeragePrice;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "用户购买次数")
    private Integer payCount;

    @ApiModelProperty(value = "是否为推广员")
    private String isPromoter;

    @ApiModelProperty(value = "成为分销员时间")
    private Date promoterTime;

    @ApiModelProperty(value = "注册类型：wechat-公众号，routine-小程序，H5-H5,iosWx-微信ios，androidWx-微信安卓，ios-ios")
    private String registerType;

    @ApiModelProperty(value = "1为正常，0为禁止")
    private String status;

    @ApiModelProperty(value = "是否注销")
    private String isLogoff;

    @ApiModelProperty(value = "注销时间")
    private Date logoffTime;

    @ApiModelProperty(value = "是否付费会员")
    private String isPaidMember;

    @ApiModelProperty(value = "是否永久付费会员")
    private String isPermanentPaidMember;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;
}
