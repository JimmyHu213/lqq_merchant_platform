package com.zbkj.admin.service;

import com.zbkj.admin.vo.MerchantMemberBenefitsVo;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;

import java.util.List;

/**
 * 管理端商户会员权益服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
public interface ManageMerchantMemberBenefitsService {

    /**
     * 获取会员权益列表
     */
    List<MerchantMemberBenefitsVo> getList();

    /**
     * 会员权益保存
     * @param benefitsVoList 会员权益类表
     */
    Boolean save(List<MerchantMemberBenefitsVo> benefitsVoList);

    /**
     * 商户会员权益预置数据处理
     */
    Boolean presetDataProcessing();
}
