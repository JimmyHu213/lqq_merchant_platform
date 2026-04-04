package com.zbkj.common.request.merchant;

import com.zbkj.common.annotation.StringContains;
import com.zbkj.common.request.UserCommonSearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商户端用户查询请求对象
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
@ApiModel(value = "MerchantUserSearchRequest对象", description = "商户端用户查询请求对象")
public class MerchantUserSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户身份：ordinary-普通，fans-粉丝，member-会员")
    @StringContains(limitValues = {"ordinary", "fans", "member"}, message = "请选择正确的用户身份")
    private String userIdentity;

    @ApiModelProperty(value = "会员等级")
    private Integer memberLevel;

    @ApiModelProperty(value = "财务状态:1-正常，0-冻结")
    private Integer financialStatus;

    @ApiModelProperty(value = "入会时间")
    private String membershipTime;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;
}
