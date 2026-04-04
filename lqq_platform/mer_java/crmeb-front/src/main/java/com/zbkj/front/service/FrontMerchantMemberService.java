package com.zbkj.front.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.MerchantCollectResponse;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.front.response.BecomeMerchantMemberResponse;
import com.zbkj.front.response.MerchantMemberLevelBenefitsResponse;
import com.zbkj.front.response.MerchantMemberUserResponse;

import java.util.List;

/**
 * 移动端商户会员服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/10
 */
public interface FrontMerchantMemberService {

    /**
     * 成为商户会员
     */
    BecomeMerchantMemberResponse becomeMember(Integer merId);

    /**
     * 获取商户会员用户信息
     */
    MerchantMemberUserResponse getMemberUserInfo(Integer merId);

    /**
     * 获取商户会员等级列表
     */
    List<MerchantMemberLevelBenefitsResponse> findMemberLevelList(Integer merId);

    /**
     * 用户入会列表
     */
    PageInfo<MerchantCollectResponse> findUserAddMerchantList(PageParamRequest pageParamRequest);

    /**
     * 商户会员注销
     */
    Boolean logoffMember(Integer merId);
}
