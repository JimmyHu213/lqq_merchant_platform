package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.dto.MemberRegisterNumDto;
import com.zbkj.common.model.merchant.MerchantMember;
import com.zbkj.common.request.DateRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author HZW
 * @description MerchantMemberService 接口
 * @date 2025-12-01
 */
public interface MerchantMemberService extends IService<MerchantMember> {

    MerchantMember getByUidAndMerId(Integer userId, Integer merId);

    /**
     * 更新商户会员金额
     *
     * @param userId         用户ID
     * @param rechargeAmount 充值金额
     * @param giftAmount     赠送金额
     * @param type           增加add、扣减sub
     * @return Boolean
     */
    Boolean updateAmount(Integer userId, Integer merId, BigDecimal rechargeAmount, BigDecimal giftAmount, String type);

    /**
     * 获取用户入会分页列表
     */
    List<MerchantMember> findUserAddMerchantList(Integer userId);

    Integer getCountByLevel(Integer level, Integer merId);

    /**
     * 清除商户会员等级
     *
     * @param level 会员等级
     */
    Boolean clearLevelByLevel(Integer level, Integer merId);

    /**
     * 商户会员数量
     *
     * @param merId 商户ID
     */
    Integer merchantMemberNum(Integer merId);

    /**
     * 商户会员新增数量
     *
     * @param merId       商户ID
     * @param dateRequest 时间范围
     */
    Integer getPaidMemberNum(Integer merId, DateRequest dateRequest);

    /**
     * 商户会员新增数量
     *
     * @param merId       商户ID
     * @param date        时间
     * @param dateRequest 时间范围
     */
    Integer getAddMemberNumByDate(Integer merId, String date, DateRequest dateRequest);

    /**
     * 获取商户会员数量
     */
    Integer getCountByMerId(Integer merId);

    /**
     * 注销商户会员
     *
     * @param userId 用户ID
     */
    Boolean logoffByUserId(Integer userId);

    /**
     * 获取商户会员来源渠道数量列表
     */
    List<MemberRegisterNumDto> findRegisterTypeNumListByMerId(Integer merId);

    /**
     * 获取新增商户会员数量
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    Integer getNewCountByMerIdAndDate(Integer merId, String startDateTime, String endDateTime);
}