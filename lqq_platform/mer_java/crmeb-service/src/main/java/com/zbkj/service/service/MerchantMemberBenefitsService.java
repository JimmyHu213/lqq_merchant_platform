package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;

import java.util.List;

/**
 * @author HZW
 * @description MerchantMemberBenefitsService 接口
 * @date 2025-12-01
 */
public interface MerchantMemberBenefitsService extends IService<MerchantMemberBenefits> {

    /**
     * 获取会员权益列表
     *
     * @param merId 商户ID
     */
    List<MerchantMemberBenefits> getList(Integer merId);

    /**
     * 获取会员权益预置数据
     * @param merId 商户ID
     */
    List<MerchantMemberBenefits> getPresetData(Integer merId);

    /**
     * 删除会员权益
     * @param merId 商户ID
     */
    Boolean deleteByMerId(Integer merId);

    /**
     * 获取权益列表
     * @param benefitsIdList 权益ID列表
     * @param merId 商户ID
     */
    List<MerchantMemberBenefits> findListByIdList(List<Integer> benefitsIdList, Integer merId);
}